package com.example.seatinventory.scenario;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.ExpireSeatHoldsUseCase;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.support.PostgresIntegrationTest;
import com.example.seatinventory.support.SectionFixtures;

/**
 * Hypothesis of ADR-0007: the expiry sweep reads every held hold, since no index covers expires_at, so under load
 * the p99 of new holds jumps at every sweep, like a stop-the-world garbage collector. Preloads 100,000 holds over
 * 1,000 sections, then sends new holds from 32 callers for 30 s while a sweep runs every 2 s, and prints the p99
 * of each second with the sweeps marked: once without an index on expires_at, by dropping the one of migration V5,
 * and once with it. See ADR-0008. Truncates the tables, so run it alone and on demand:
 * {@code ./mvnw verify -Dit.test=SweepLoadScenario}
 */
@RequiredArgsConstructor
class SweepLoadScenario extends PostgresIntegrationTest {

    private static final int PRELOADED_SECTIONS = 1_000;
    private static final int HOLDS_PER_SECTION = 100;
    private static final int HOT_SECTIONS = 20;
    private static final int HOT_SECTION_CAPACITY = 100_000;
    private static final int CALLERS = 32;
    private static final Duration WARM_UP = Duration.ofSeconds(5);
    private static final Duration LOAD = Duration.ofSeconds(30);
    private static final Duration SWEEP_INTERVAL = Duration.ofSeconds(2);
    private static final String EXPIRY_INDEX = "seat_hold_held_expires_at_idx";

    private final OpenSectionUseCase openSection;

    private final ReserveSeatUseCase reserveSeat;

    private final ExpireSeatHoldsUseCase expireSeatHolds;

    private final JdbcClient jdbcClient;

    private record Call(Instant at, long latencyMicros) {
    }

    private record Sweep(Instant at, long findMillis, long totalMillis, int sections) {
    }

    private record LoadRun(Instant start, List<Call> calls, List<Sweep> sweeps) {

        long secondOf(Instant instant) {
            return Duration.between(start, instant).toSeconds();
        }
    }

    @Test
    void withoutAnIndexOnExpiry() {
        preloadHolds();
        jdbcClient.sql("DROP INDEX IF EXISTS " + EXPIRY_INDEX).update();

        LoadRun run = runLoad("no index on expires_at");

        assertThat(run.sweeps()).isNotEmpty();
    }

    @Test
    void withAPartialIndexOnExpiry() {
        preloadHolds();
        jdbcClient.sql("CREATE INDEX IF NOT EXISTS " + EXPIRY_INDEX
            + " ON seat_hold (expires_at) WHERE status = 'HELD'").update();

        LoadRun run = runLoad("partial index on expires_at");

        assertThat(run.sweeps()).isNotEmpty();
    }

    /**
     * Per section: 70 confirmed and 25 expired holds from before, and 5 held ones that expire 6, 12, 18, 24 and
     * 30 s from now, so every sweep during the load has work. The section reserves the 75 active seats.
     */
    private void preloadHolds() {
        jdbcClient.sql("TRUNCATE seat_hold, section").update();
        jdbcClient.sql("""
                INSERT INTO section (id, code, title, capacity, reserved_seats)
                SELECT gen_random_uuid(), 'LOAD' || n, 'Load section', 1000, 75
                FROM generate_series(1, :sections) AS n
                """)
            .param("sections", PRELOADED_SECTIONS)
            .update();
        jdbcClient.sql("""
                INSERT INTO seat_hold (id, section_id, student_id, held_at, expires_at, status)
                SELECT gen_random_uuid(), s.id, gen_random_uuid(), now() - interval '10 minutes',
                       CASE WHEN k <= 95 THEN now() - interval '5 minutes'
                            ELSE now() + make_interval(secs => (k - 95) * 6) END,
                       CASE WHEN k <= 70 THEN 'CONFIRMED' WHEN k <= 95 THEN 'EXPIRED' ELSE 'HELD' END
                FROM section s CROSS JOIN generate_series(1, :holds) AS k
                """)
            .param("holds", HOLDS_PER_SECTION)
            .update();
    }

    /**
     * Callers run for {@link #WARM_UP} before the measured load, so that the first variant does not pay for a
     * cold JVM and database alone. The embedded server runs with fsync off, set on its command line, which makes
     * writes cheaper than on a real server: the numbers only compare the two variants.
     */
    private LoadRun runLoad(String label) {
        jdbcClient.sql("ANALYZE seat_hold").update();
        List<UUID> hotSections = IntStream.range(0, HOT_SECTIONS)
            .mapToObj(index -> openSection.openSection(
                SectionFixtures.openSectionCommand(HOT_SECTION_CAPACITY)).sectionId())
            .toList();
        holdUntil(Instant.now().plus(WARM_UP), hotSections, 0);
        String before = settings();
        LoadRun run = measure(hotSections);
        report(label + " (before the run: " + before + ")", run);
        return run;
    }

    private LoadRun measure(List<UUID> hotSections) {
        Instant start = Instant.now();
        List<Sweep> sweeps = Collections.synchronizedList(new ArrayList<>());
        try (ScheduledExecutorService sweeper = Executors.newSingleThreadScheduledExecutor()) {
            long interval = SWEEP_INTERVAL.toMillis();
            sweeper.scheduleWithFixedDelay(() -> sweeps.add(sweep()), interval, interval, TimeUnit.MILLISECONDS);
            List<Call> calls = holdUntil(start.plus(LOAD), hotSections, 1);
            return new LoadRun(start, calls, List.copyOf(sweeps));
        }
    }

    private Sweep sweep() {
        Instant at = Instant.now();
        long startedAt = System.nanoTime();
        List<UUID> sectionIds = expireSeatHolds.findSectionsWithExpiredHolds();
        long findMillis = (System.nanoTime() - startedAt) / 1_000_000;
        sectionIds.forEach(expireSeatHolds::expireHoldsIn);
        return new Sweep(at, findMillis, (System.nanoTime() - startedAt) / 1_000_000, sectionIds.size());
    }

    /**
     * Every run uses students of its own, so that no request finds a hold from an earlier run.
     */
    private List<Call> holdUntil(Instant deadline, List<UUID> hotSections, int run) {
        try (ExecutorService callers = Executors.newFixedThreadPool(CALLERS)) {
            List<Callable<List<Call>>> tasks = IntStream.range(0, CALLERS)
                .<Callable<List<Call>>>mapToObj(caller -> () -> callerHolds(deadline, hotSections, run, caller))
                .toList();
            List<Call> calls = new ArrayList<>();
            for (Future<List<Call>> result : callers.invokeAll(tasks)) {
                calls.addAll(result.get());
            }
            return calls;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while holding seats", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("A caller failed", exception.getCause());
        }
    }

    private List<Call> callerHolds(Instant deadline, List<UUID> hotSections, int run, int caller) {
        List<Call> calls = new ArrayList<>();
        long studentPrefix = ((long) run << 32) | (caller + 1L);
        for (long request = 0; Instant.now().isBefore(deadline); request++) {
            UUID sectionId = hotSections.get((int) ((caller + request) % hotSections.size()));
            Instant at = Instant.now();
            long startedAt = System.nanoTime();
            reserveSeat.reserveSeat(new ReserveSeatCommand(sectionId, new UUID(studentPrefix, request)));
            calls.add(new Call(at, (System.nanoTime() - startedAt) / 1_000));
        }
        return calls;
    }

    private void report(String label, LoadRun run) {
        Set<Long> sweepSeconds = run.sweeps().stream()
            .flatMap(sweep -> LongStream.rangeClosed(run.secondOf(sweep.at()),
                run.secondOf(sweep.at().plusMillis(sweep.totalMillis()))).boxed())
            .collect(Collectors.toSet());
        String prefix = "SCENARIO " + label + ": ";
        System.out.println(prefix + "after the run: " + settings());
        System.out.println(prefix + "sweep query plan: " + planOfTheSweepQuery());
        reportSeconds(prefix, run, sweepSeconds);
        System.out.printf("%scalls=%d p99 all=%.1fms in sweep seconds=%.1fms other seconds=%.1fms%n", prefix,
            run.calls().size(), p99(run.calls(), call -> true),
            p99(run.calls(), call -> sweepSeconds.contains(run.secondOf(call.at()))),
            p99(run.calls(), call -> !sweepSeconds.contains(run.secondOf(call.at()))));
        System.out.printf("%ssweeps=%d sections swept=%d find query max=%dms avg=%.1fms whole sweep max=%dms%n",
            prefix, run.sweeps().size(), run.sweeps().stream().mapToInt(Sweep::sections).sum(),
            run.sweeps().stream().mapToLong(Sweep::findMillis).max().orElse(0),
            run.sweeps().stream().mapToLong(Sweep::findMillis).average().orElse(0),
            run.sweeps().stream().mapToLong(Sweep::totalMillis).max().orElse(0));
    }

    private void reportSeconds(String prefix, LoadRun run, Set<Long> sweepSeconds) {
        Map<Long, List<Call>> callsBySecond = run.calls().stream()
            .collect(groupingBy(call -> run.secondOf(call.at()), TreeMap::new, toList()));
        callsBySecond.forEach((second, calls) -> System.out.printf("%ssecond=%02d calls=%d p99=%.1fms%s%n",
            prefix, second, calls.size(), p99(calls, call -> true), sweepSeconds.contains(second) ? " sweep" : ""));
    }

    private static double p99(List<Call> calls, Predicate<Call> selected) {
        List<Long> latencies = calls.stream().filter(selected).map(Call::latencyMicros).sorted().toList();
        if (latencies.isEmpty()) {
            return 0;
        }
        return latencies.get(Math.ceilDiv(latencies.size() * 99, 100) - 1) / 1000.0;
    }

    private String settings() {
        return jdbcClient.sql("""
                SELECT 'fsync=' || current_setting('fsync')
                    || ' synchronous_commit=' || current_setting('synchronous_commit')
                    || ' held holds=' || (SELECT count(*) FROM seat_hold WHERE status = 'HELD')
                    || ' all holds=' || (SELECT count(*) FROM seat_hold)
                """)
            .query(String.class)
            .single();
    }

    private String planOfTheSweepQuery() {
        return jdbcClient.sql("""
                EXPLAIN SELECT DISTINCT section_id FROM seat_hold WHERE status = 'HELD' AND expires_at <= now()
                """)
            .query(String.class)
            .list()
            .stream()
            .filter(line -> line.contains("Scan"))
            .map(String::strip)
            .collect(Collectors.joining(" | "));
    }
}
