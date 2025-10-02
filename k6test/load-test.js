import http from 'k6/http';
import { check } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.6.0/index.js';

export const options = {
    scenarios: {
        constant_rate_test: {
            executor: 'constant-arrival-rate',
            rate: 750,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 750,
        },
    },
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.01'],
    },
    insecureSkipTLSVerify: true, // <- aqui
};

export default function () {
    const url = 'https://localhost:8080/entity';

    const payload = JSON.stringify({
        id: uuidv4(),
        level: Math.floor(Math.random() * 100) + 1,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 201': (r) => r.status === 201,
    });
}
