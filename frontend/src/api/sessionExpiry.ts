import { RequestFailure } from './requestFailure';

const reloadTimeKey = 'life-balance.session-reload-time';
const reloadIntervalMilliseconds = 30_000;
const sessionEndedDetail = 'Your session has ended. Reload the page to sign in again.';

export function rejectExpiredSession(): never {
  const now = Date.now();
  if (isReloadDue(storedReloadTime(), now)) {
    reloadRecordingTime(now);
  }
  throw new RequestFailure({ status: 401, detail: sessionEndedDetail, fieldErrors: {} });
}

function isReloadDue(lastReloadTime: number | null, now: number): boolean {
  return lastReloadTime !== null && now - lastReloadTime >= reloadIntervalMilliseconds;
}

function storedReloadTime(): number | null {
  try {
    const stored = Number(window.sessionStorage.getItem(reloadTimeKey));
    return Number.isFinite(stored) ? stored : 0;
  } catch {
    return null;
  }
}

function reloadRecordingTime(now: number): void {
  try {
    window.sessionStorage.setItem(reloadTimeKey, String(now));
  } catch {
    return;
  }
  window.location.reload();
}
