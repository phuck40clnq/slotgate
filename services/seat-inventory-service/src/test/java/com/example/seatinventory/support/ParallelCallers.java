package com.example.seatinventory.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;

/**
 * Releases many callers at the same moment and counts who got a seat. Any failure other than a full
 * section fails the run, so an unexpected error cannot pass as a rejection.
 */
public final class ParallelCallers {

    private ParallelCallers() {
    }

    public record Outcome(int accepted, int rejected, long elapsedMillis, List<Long> sortedLatenciesMillis) {

        public long latencyPercentile(int percentile) {
            int rank = Math.ceilDiv(sortedLatenciesMillis.size() * percentile, 100);
            return sortedLatenciesMillis.get(rank - 1);
        }
    }

    private record Call(boolean isAccepted, long latencyNanos) {
    }

    public static Outcome run(int callers, IntConsumer call) {
        CountDownLatch startLine = new CountDownLatch(callers);
        List<Callable<Call>> tasks = IntStream.range(0, callers)
            .<Callable<Call>>mapToObj(callerIndex -> () -> callAtTheSameMoment(startLine, call, callerIndex))
            .toList();
        long startedAt = System.nanoTime();
        List<Call> calls = runAll(tasks);
        return outcomeOf(calls, System.nanoTime() - startedAt);
    }

    private static Call callAtTheSameMoment(CountDownLatch startLine, IntConsumer call, int callerIndex)
            throws InterruptedException {
        startLine.countDown();
        startLine.await();
        long startedAt = System.nanoTime();
        boolean isAccepted = isAccepted(call, callerIndex);
        return new Call(isAccepted, System.nanoTime() - startedAt);
    }

    private static boolean isAccepted(IntConsumer call, int callerIndex) {
        try {
            call.accept(callerIndex);
            return true;
        } catch (BusinessException exception) {
            if (exception.getErrorCode() != SeatInventoryBusinessErrorCode.SECTION_FULL) {
                throw exception;
            }
            return false;
        }
    }

    private static List<Call> runAll(List<Callable<Call>> tasks) {
        try (ExecutorService executor = Executors.newFixedThreadPool(tasks.size())) {
            List<Call> calls = new ArrayList<>();
            for (Future<Call> result : executor.invokeAll(tasks)) {
                calls.add(result.get());
            }
            return calls;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for callers", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("A caller failed unexpectedly", exception.getCause());
        }
    }

    private static Outcome outcomeOf(List<Call> calls, long elapsedNanos) {
        int accepted = (int) calls.stream().filter(Call::isAccepted).count();
        List<Long> latencies = calls.stream()
            .map(call -> call.latencyNanos() / 1_000_000)
            .sorted(Comparator.naturalOrder())
            .toList();
        return new Outcome(accepted, calls.size() - accepted, elapsedNanos / 1_000_000, latencies);
    }
}
