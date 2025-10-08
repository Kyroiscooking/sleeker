import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.6.0/index.js';

export const options = {
    stages: [
        { duration: '1m', target: 250 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 350 },
        { duration: '1m', target: 500 },
        { duration: '2m', target: 500 },
        { duration: '1m', target: 0 },
    ],

    insecureSkipTLSVerify: true,

    thresholds: {
        http_req_duration: ['p(99)<5'],
        http_req_failed: ['rate<0.01'],
        checks: ['rate>0.99'],
    },
};

const URL = 'https://localhost:8080/entity';

export default function () {
    const isPost = Math.random() < 0.5;

    let res;

    if (isPost) {
        const payload = JSON.stringify({
            id: uuidv4(),
            level: Math.floor(Math.random() * 100) + 1,
        });

        const params = {
            headers: { 'Content-Type': 'application/json' },
        };

        res = http.post(URL, payload, params);

        check(res, {
            'POST status 201': (r) => r.status === 201,
            'POST tempo < 5ms': (r) => r.timings.duration < 5,
        });

    } else {
        res = http.get(URL);

        check(res, {
            'GET status 200': (r) => r.status === 200,
            'GET tempo < 5ms': (r) => r.timings.duration < 5,
        });
    }

    sleep(1);
}
