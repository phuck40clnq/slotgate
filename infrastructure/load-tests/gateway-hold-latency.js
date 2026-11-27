// Measures how long holding a seat takes, straight to seat-inventory-service or through the gateway.
// Every request holds a seat for a new (student, section) pair, so it always takes the path that claims a seat:
// a pair is unique (ADR-0006), and repeating one returns the existing hold through another path.
import http from 'k6/http';
import { check } from 'k6';
import exec from 'k6/execution';
import { SharedArray } from 'k6/data';

const ROUTE = __ENV.ROUTE;
const RUN_ID = __ENV.RUN_ID;
const VUS = Number(__ENV.VUS || 10);
const WARMUP_HOLDS = Number(__ENV.WARMUP_HOLDS || 200);
const MEASURED_HOLDS = Number(__ENV.MEASURED_HOLDS || 2000);
const SEAT_INVENTORY_URL = __ENV.SEAT_INVENTORY_URL || 'http://localhost:8084';
const GATEWAY_URL = __ENV.GATEWAY_URL || 'http://localhost:8080';

// Each entry is {"student": "<uuid>", "token": "<token whose sub is that student>"}.
const STUDENTS = new SharedArray('students', () => JSON.parse(open(__ENV.STUDENTS_FILE)));

if (ROUTE !== 'direct' && ROUTE !== 'gateway') {
  throw new Error('ROUTE must be direct or gateway');
}
if (!RUN_ID) {
  throw new Error('RUN_ID must name the run, so that its section codes are new');
}

const TOTAL_HOLDS = WARMUP_HOLDS + MEASURED_HOLDS;
// Each section takes one hold per student, so it needs as many seats as there are students.
const SECTION_COUNT = Math.ceil(TOTAL_HOLDS / STUDENTS.length);

export const options = {
  scenarios: {
    holds: {
      executor: 'shared-iterations',
      vus: VUS,
      iterations: TOTAL_HOLDS,
      maxDuration: '10m',
    },
  },
  summaryTrendStats: ['min', 'med', 'p(90)', 'p(99)', 'max', 'count'],
  // The thresholds make the summary report the measured holds apart from the warm-up and the setup.
  thresholds: {
    'http_req_duration{phase:measure}': ['max>=0'],
    'checks{phase:measure}': ['rate==1'],
  },
};

export function setup() {
  const sectionIds = [];
  for (let index = 0; index < SECTION_COUNT; index++) {
    const response = http.post(
      `${SEAT_INVENTORY_URL}/api/v1/seat-inventory/sections`,
      JSON.stringify({ code: `load-${RUN_ID}-${index}`, title: 'Load test section', capacity: STUDENTS.length }),
      { headers: { 'Content-Type': 'application/json' }, tags: { phase: 'setup' } },
    );
    if (response.status !== 201) {
      throw new Error(`Opening section ${index} answered ${response.status}: ${response.body}`);
    }
    sectionIds.push(response.json('sectionId'));
  }
  return { sectionIds };
}

export default function (data) {
  const iteration = exec.scenario.iterationInTest;
  const student = STUDENTS[iteration % STUDENTS.length];
  const sectionId = data.sectionIds[Math.floor(iteration / STUDENTS.length)];
  const phase = iteration < WARMUP_HOLDS ? 'warmup' : 'measure';

  const response = ROUTE === 'direct'
    ? http.post(`${SEAT_INVENTORY_URL}/api/v1/seat-inventory/sections/${sectionId}/holds`, null, {
      headers: { 'X-Internal-User-Id': student.student },
      tags: { phase },
    })
    : http.post(`${GATEWAY_URL}/api/seat-inventory/sections/${sectionId}/holds`, null, {
      headers: { Authorization: `Bearer ${student.token}` },
      tags: { phase },
    });

  check(response, { 'a new seat is held (201)': (r) => r.status === 201 }, { phase });
}
