import { http } from 'msw';
import { problem } from './responses';

export const contentNegotiationHandlers = [http.all('/api/*', ({ request }) => refusalOf(request))];

function refusalOf(request: Request): Response | undefined {
  if (request.headers.get('Accept') !== 'application/json') {
    return problem(406, 'Life Balance answers in JSON');
  }
  const isJsonBody = request.headers.get('Content-Type') === 'application/json';
  return request.body === null || isJsonBody
    ? undefined
    : problem(415, 'Send the request body as JSON');
}
