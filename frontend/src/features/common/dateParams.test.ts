import { expect, test } from 'vitest';
import { resolveDay, resolveMonday } from './dateParams';

test('uses the fallback Monday when the address names no week', () => {
  expect(resolveMonday(undefined, '2026-09-21')).toEqual({ kind: 'monday', monday: '2026-09-21' });
});

test('accepts a Monday', () => {
  expect(resolveMonday('2026-09-14', '2026-09-21')).toEqual({
    kind: 'monday',
    monday: '2026-09-14',
  });
});

test('redirects another day to the Monday of its week', () => {
  expect(resolveMonday('2026-09-16', '2026-09-21')).toEqual({
    kind: 'redirect',
    monday: '2026-09-14',
  });
});

test('rejects a week that is not a date', () => {
  expect(resolveMonday('next-week', '2026-09-21')).toEqual({ kind: 'invalid' });
});

test('uses the fallback day when the address names no day', () => {
  expect(resolveDay(undefined, '2026-09-23')).toEqual({ kind: 'day', day: '2026-09-23' });
});

test('accepts any valid day and rejects anything else', () => {
  expect(resolveDay('2026-09-24', '2026-09-23')).toEqual({ kind: 'day', day: '2026-09-24' });
  expect(resolveDay('2026-02-30', '2026-09-23')).toEqual({ kind: 'invalid' });
});
