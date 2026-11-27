# infrastructure/load-tests

The k6 scripts that measure the services under load. For anyone who measures a latency or tests a prediction about one.

## Why it exists

A prediction about performance is only checked by a run. Each script is one scenario, run against the services as they run on a machine, so a result can be repeated and compared. Why k6: [ADR-0011](../../docs/adr/0011-use-k6-for-load-tests.md).

## Configuration

`gateway-hold-latency.js` holds seats with a new (student, section) pair per request and reads these variables, passed as `-e NAME=value`:

| Variable | Meaning |
|---|---|
| `ROUTE` | `direct` sends holds to `seat-inventory-service`, `gateway` sends them through the gateway |
| `RUN_ID` | Names the run; the codes of the sections it opens start with `load-<RUN_ID>-`, so each run needs a new one |
| `STUDENTS_FILE` | A JSON array of `{"student": "<UUID>", "token": "<token>"}`, where each token is one the gateway accepts and whose `sub` is that student |
| `VUS` | Concurrent virtual users; defaults to `10` |
| `WARMUP_HOLDS`, `MEASURED_HOLDS` | Holds sent before measuring and measured holds; default to `200` and `2000` |
| `SEAT_INVENTORY_URL`, `GATEWAY_URL` | Default to `http://localhost:8084` and `http://localhost:8080` |

Every section gets as many seats as there are students, and a run opens as many sections as it needs.

## Run

Install k6, start `seat-inventory-service`, `auth-service` and the gateway with the `local` profile as their READMEs show, then run each route from the repository root:

```bash
k6 run -e ROUTE=direct -e RUN_ID=r1-direct -e STUDENTS_FILE=students.json infrastructure/load-tests/gateway-hold-latency.js
k6 run -e ROUTE=gateway -e RUN_ID=r1-gateway -e STUDENTS_FILE=students.json infrastructure/load-tests/gateway-hold-latency.js
```

The summary line `{ phase:measure }` under `http_req_duration` gives the measured holds; `checks{phase:measure}` fails when a hold did not answer `201`.

## Details

- Each run leaves its sections and holds in the local database of `seat-inventory-service`.
- Stop builds on the machine while measuring, and record the conditions with the result, as ADR-0011 does.
