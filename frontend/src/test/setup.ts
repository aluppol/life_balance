import '@testing-library/jest-dom/vitest';
import { cleanup, configure } from '@testing-library/react';
import { afterAll, afterEach, beforeAll, beforeEach, vi } from 'vitest';
import { resetPlanner } from './planner';
import { server } from './server';
import { wednesdayMorning } from './time';

configure({ asyncUtilTimeout: 3000 });

beforeAll(() => {
  server.listen({ onUnhandledRequest: 'error' });
});

beforeEach(() => {
  resetPlanner();
  vi.useFakeTimers({ toFake: ['Date'], now: wednesdayMorning, shouldAdvanceTime: true });
});

afterEach(() => {
  cleanup();
  server.resetHandlers();
  vi.useRealTimers();
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
  window.sessionStorage.clear();
});

afterAll(() => {
  server.close();
});
