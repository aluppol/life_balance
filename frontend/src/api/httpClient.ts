import { parseProblemDetails } from './problemDetails';
import { RequestFailure } from './requestFailure';
import { rejectExpiredSession } from './sessionExpiry';

const unreachableDetail = 'Life Balance could not be reached. Check your connection and try again.';
const jsonHeaders = { Accept: 'application/json', 'Content-Type': 'application/json' };

export async function getJson<Body>(path: string): Promise<Body> {
  const response = await exchange(path, {});
  await rejectFailure(response);
  return (await response.json()) as Body;
}

export async function getOptionalJson<Body>(path: string): Promise<Body | null> {
  const response = await exchange(path, {});
  if (response.status === 404) {
    return null;
  }
  await rejectFailure(response);
  return (await response.json()) as Body;
}

export async function postJson(path: string, body: unknown): Promise<void> {
  await send(path, { method: 'POST', body: JSON.stringify(body) });
}

export async function putJson(path: string, body: unknown): Promise<void> {
  await send(path, { method: 'PUT', body: JSON.stringify(body) });
}

export async function putEmpty(path: string): Promise<void> {
  await send(path, { method: 'PUT' });
}

export async function deleteAt(path: string): Promise<void> {
  await send(path, { method: 'DELETE' });
}

async function send(path: string, init: RequestInit): Promise<void> {
  await rejectFailure(await exchange(path, init));
}

async function exchange(path: string, init: RequestInit): Promise<Response> {
  try {
    return await fetch(path, { ...init, headers: jsonHeaders });
  } catch {
    throw new RequestFailure({ status: 0, detail: unreachableDetail, fieldErrors: {} });
  }
}

async function rejectFailure(response: Response): Promise<void> {
  if (response.ok) {
    return;
  }
  if (response.status === 401) {
    rejectExpiredSession();
  }
  throw new RequestFailure(parseProblemDetails(response.status, await response.text()));
}
