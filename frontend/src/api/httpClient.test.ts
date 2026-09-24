import { http, HttpResponse } from 'msw';
import { expect, test, vi } from 'vitest';
import { problem } from '../test/handlers/responses';
import { planner } from '../test/planner';
import { stubPageReload } from '../test/renderRoute';
import { server } from '../test/server';
import { wednesdayMorning } from '../test/time';
import { deleteAt, getJson, getOptionalJson, postJson, putEmpty } from './httpClient';
import { fieldErrorsOf, RequestFailure } from './requestFailure';

const sessionEnded = 'Your session has ended. Reload the page to sign in again.';

function expireTheSession(): void {
  server.use(http.get('/api/me', () => new HttpResponse(null, { status: 401 })));
}

test('answers the JSON body of a successful request', async () => {
  expect(await getJson('/api/me')).toEqual({ displayName: 'Ada Lovelace', isGuest: false });
});

test('answers nothing for a resource that does not exist yet', async () => {
  planner().mission = null;
  expect(await getOptionalJson('/api/mission')).toBeNull();
  planner().mission = { text: 'Be kind.' };
  expect(await getOptionalJson('/api/mission')).toEqual({ text: 'Be kind.' });
});

test('turns a problem detail into a request failure with its detail', async () => {
  const failure: unknown = await postJson('/api/values', {
    name: 'integrity',
    description: '',
  }).catch((error: unknown) => error);
  expect(failure).toBeInstanceOf(RequestFailure);
  expect(failure).toMatchObject({
    name: 'RequestFailure',
    message: "A core value named 'integrity' already exists",
    problem: { status: 409 },
  });
});

test('keeps the field errors of an invalid request', async () => {
  const failure = await postJson('/api/values', { name: ' ', description: '' }).then(
    () => null,
    (error: unknown) => (error instanceof Error ? error : null),
  );
  expect(fieldErrorsOf(failure)).toEqual({ name: 'must not be blank' });
  expect(fieldErrorsOf(new Error('plain'))).toEqual({});
  expect(fieldErrorsOf(null)).toEqual({});
});

test('sends commands without a body and succeeds on no content', async () => {
  await putEmpty('/api/activities/activity-tempo/completion');
  expect(
    planner().activities.find((activity) => activity.id === 'activity-tempo')?.isCompleted,
  ).toBe(true);
  await deleteAt('/api/activities/activity-tempo');
  expect(planner().activities.some((activity) => activity.id === 'activity-tempo')).toBe(false);
});

test('reports a server that cannot be reached', async () => {
  server.use(http.get('/api/me', () => HttpResponse.error()));
  await expect(getJson('/api/me')).rejects.toThrow(
    'Life Balance could not be reached. Check your connection and try again.',
  );
});

test('reports a failure without a readable body by its status', async () => {
  server.use(http.get('/api/me', () => new HttpResponse('<html>', { status: 502 })));
  await expect(getJson('/api/me')).rejects.toThrow(
    'Life Balance could not complete the request. Please try again.',
  );
});

test('reloads the page once when the session has expired', async () => {
  const reload = stubPageReload();
  expireTheSession();
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  expect(reload).toHaveBeenCalledTimes(1);
});

test('reloads again only 30 seconds after the last reload', async () => {
  vi.useFakeTimers({ toFake: ['Date'], now: wednesdayMorning });
  const reload = stubPageReload();
  expireTheSession();
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  vi.setSystemTime(wednesdayMorning.getTime() + 29_999);
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  expect(reload).toHaveBeenCalledTimes(1);
  vi.setSystemTime(wednesdayMorning.getTime() + 30_000);
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  expect(reload).toHaveBeenCalledTimes(2);
});

test('does not reload when the reload time cannot be remembered', async () => {
  const reload = stubPageReload();
  vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
    throw new DOMException('The quota has been exceeded.', 'QuotaExceededError');
  });
  expireTheSession();
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  expect(reload).not.toHaveBeenCalled();
});

test('does not reload when the session storage cannot be read', async () => {
  const reload = stubPageReload();
  vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
    throw new DOMException('Storage is disabled.', 'SecurityError');
  });
  expireTheSession();
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  expect(reload).not.toHaveBeenCalled();
});

test('treats an unreadable remembered reload time as no reload yet', async () => {
  const reload = stubPageReload();
  window.sessionStorage.setItem('life-balance.session-reload-time', 'yesterday');
  expireTheSession();
  const beforeReload = Date.now();
  await expect(getJson('/api/me')).rejects.toThrow(sessionEnded);
  expect(reload).toHaveBeenCalledTimes(1);
  const remembered = Number(window.sessionStorage.getItem('life-balance.session-reload-time'));
  expect(remembered).toBeGreaterThanOrEqual(beforeReload);
});

test('answers a missing resource that is required with its problem detail', async () => {
  server.use(http.get('/api/me', () => problem(404, 'Nobody is signed in')));
  await expect(getJson('/api/me')).rejects.toThrow('Nobody is signed in');
});
