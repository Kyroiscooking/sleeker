debian:k6test$ k6 run load-test.js
```
         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 
```

     execution: local
        script: load-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 250 max VUs, 1m30s max duration (incl. graceful stop):
              * constant_rate_test: 250.00 iterations/s for 1m0s (maxVUs: 250, gracefulStop: 30s)



█ THRESHOLDS

    http_req_duration
    ✓ 'p(99)<1000' p(99)=628.18µs

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%


█ TOTAL RESULTS

    checks_total.......: 15001   250.010359/s
    checks_succeeded...: 100.00% 15001 out of 15001
    checks_failed......: 0.00%   0 out of 15001

    ✓ status is 201

    HTTP
    http_req_duration..............: avg=352.62µs min=50.28µs  med=376.15µs max=8.23ms  p(90)=479.39µs p(95)=520.46µs
      { expected_response:true }...: avg=352.62µs min=50.28µs  med=376.15µs max=8.23ms  p(90)=479.39µs p(95)=520.46µs
    http_req_failed................: 0.00%  0 out of 15001
    http_reqs......................: 15001  250.010359/s

    EXECUTION
    iteration_duration.............: avg=726.86µs min=115.92µs med=786.4µs  max=40.86ms p(90)=934.24µs p(95)=991.8µs 
    iterations.....................: 15001  250.010359/s
    vus............................: 1      min=0          max=1  
    vus_max........................: 250    min=250        max=250

    NETWORK
    data_received..................: 2.2 MB 37 kB/s
    data_sent......................: 2.8 MB 46 kB/s




running (1m00.0s), 000/250 VUs, 15001 complete and 0 interrupted iterations
constant_rate_test ✓ [======================================] 000/250 VUs  1m0s  250.00 iters/s
