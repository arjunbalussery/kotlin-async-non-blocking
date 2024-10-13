import http from 'k6/http';
import { check, sleep } from 'k6';
//import { v4 as uuidv4 } from 'uuid';

export let options = {
  vus: 20, // Number of virtual users (VUs)
  duration: '10s', // Duration of the test
};

export default function () {
  // Generate a unique UUID for each request
 // let uniqueUUID = uuidv4();

  // Make a GET request with the unique UUID
  let response = http.get(`http://localhost:8080/api/v1/c1c4f03c-04ff-453f-81d9-c1ec4867f4c1`);

  // Check if the response status code is 200
  check(response, {
    'is status 200': (r) => r.status === 200,
  });

  // Sleep for a short duration between requests
  sleep(1); // adjust as needed
}