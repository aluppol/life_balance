import { HttpResponse } from 'msw';

const problemHeaders = { 'Content-Type': 'application/problem+json' };

export function problem(status: number, detail: string): Response {
  return HttpResponse.json(
    { type: 'about:blank', status, detail },
    { status, headers: problemHeaders },
  );
}

export function invalidFields(errors: Readonly<Record<string, string>>): Response {
  return HttpResponse.json(
    { type: 'about:blank', status: 400, detail: 'Some fields are invalid', errors },
    { status: 400, headers: problemHeaders },
  );
}

export function created(body: object, location: string): Response {
  return HttpResponse.json(body, { status: 201, headers: { Location: location } });
}

export function noContent(): Response {
  return new HttpResponse(null, { status: 204 });
}
