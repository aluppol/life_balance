import { expect, test } from 'vitest';
import type { Goal } from '../../api/goals';
import { seededValues } from '../../test/seed';
import { detailsOfGoalDraft, draftOfGoal, emptyGoalDraft, valueChoices } from './goalDrafts';
import { isActive, matchesFilter } from './goalStatuses';

const goal: Goal = {
  id: 'goal-offline',
  roleId: 'role-engineer',
  title: 'Ship offline mode',
  description: 'Sync that never loses a change.',
  dueOn: null,
  status: 'DROPPED',
  valueIds: ['value-integrity'],
};

test('starts a new goal for a role with nothing else filled in', () => {
  expect(emptyGoalDraft('role-parent')).toEqual({
    roleId: 'role-parent',
    title: '',
    description: '',
    dueOn: '',
    valueIds: [],
  });
});

test('edits a goal without a due date as an empty date field', () => {
  expect(draftOfGoal(goal)).toEqual({
    roleId: 'role-engineer',
    title: 'Ship offline mode',
    description: 'Sync that never loses a change.',
    dueOn: '',
    valueIds: ['value-integrity'],
  });
});

test('sends an empty due date as no due date and keeps a chosen one', () => {
  expect(detailsOfGoalDraft(draftOfGoal(goal)).dueOn).toBeNull();
  expect(detailsOfGoalDraft({ ...draftOfGoal(goal), dueOn: '2026-12-01' }).dueOn).toBe(
    '2026-12-01',
  );
});

test('offers every value as a choice', () => {
  expect(valueChoices(seededValues).map((choice) => choice.label)).toEqual([
    'Integrity',
    'Family',
    'Growth',
    'Health',
  ]);
  expect(valueChoices(seededValues)[0]?.value).toBe('value-integrity');
});

test('matches goals against the status filter', () => {
  expect(matchesFilter(goal, 'ALL')).toBe(true);
  expect(matchesFilter(goal, 'DROPPED')).toBe(true);
  expect(matchesFilter(goal, 'ACTIVE')).toBe(false);
  expect(isActive(goal)).toBe(false);
  expect(isActive({ ...goal, status: 'ACTIVE' })).toBe(true);
});
