import { expect, test } from 'vitest';
import { seededGoals, seededRoles, seededValues } from '../../test/seed';
import { activeGoalCount, goalsByRole, valueNamesOf } from './goalBoard';

function titlesByRole(filter: Parameters<typeof goalsByRole>[2]): Record<string, string[]> {
  return Object.fromEntries(
    goalsByRole(seededRoles, seededGoals, filter).map(({ role, goals }) => [
      role.name,
      goals.map((goal) => goal.title),
    ]),
  );
}

test('groups the goals under every role in role order', () => {
  expect(Object.keys(titlesByRole('ALL'))).toEqual([
    'Parent',
    'Engineer',
    'Sharpen the Saw',
    'Friend',
  ]);
  expect(titlesByRole('ALL')['Sharpen the Saw']).toEqual([
    'Run a 10K under 55 minutes',
    'Re-read The 7 Habits',
  ]);
});

test('keeps only the goals with the chosen status', () => {
  expect(titlesByRole('ACTIVE')['Sharpen the Saw']).toEqual(['Run a 10K under 55 minutes']);
  expect(titlesByRole('ACHIEVED')['Sharpen the Saw']).toEqual(['Re-read The 7 Habits']);
  expect(titlesByRole('DROPPED').Parent).toEqual([]);
  expect(titlesByRole('ACTIVE').Friend).toEqual([]);
});

test('names the values a goal serves in value rank order', () => {
  const goal = { ...seededGoals[0], valueIds: ['value-growth', 'value-family'] };
  expect(valueNamesOf(goal as (typeof seededGoals)[number], seededValues)).toEqual([
    'Family',
    'Growth',
  ]);
});

test('counts the active goals of a role', () => {
  expect(activeGoalCount(seededGoals, 'role-saw')).toBe(1);
  expect(activeGoalCount(seededGoals, 'role-parent')).toBe(1);
  expect(activeGoalCount(seededGoals, 'role-friend')).toBe(0);
});
