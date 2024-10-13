import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 90,       // number of virtual users
  duration: '10s',  // duration of the test
};

export default function () {
  // URL of the API endpoint
  const apiUrl = 'http://localhost:8080/api/v2';

  // Request headers, adjust as needed
  const headers = {
    'Content-Type': 'application/json',
  };

  // Request payload, adjust as needed
  const payload = JSON.stringify({
    name: 'value1'
  });

  // Send POST request
  const response = http.post(apiUrl, payload, { headers: headers });

  // Check if the request was successful (status code 2xx)
  check(response, {
    'is success': (r) => r.status === 200 || r.status === 201,
  });

  // Add a sleep to control the request rate
  sleep(1);
}