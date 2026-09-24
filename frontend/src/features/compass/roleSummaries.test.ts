import { expect, test } from 'vitest';
import { activeGoalSummary } from './roleSummaries';

test('summarises the active goals of a role in words', () => {
  expect(activeGoalSummary(0)).toBe('No active goals');
  expect(activeGoalSummary(1)).toBe('1 active goal');
  expect(activeGoalSummary(3)).toBe('3 active goals');
});
