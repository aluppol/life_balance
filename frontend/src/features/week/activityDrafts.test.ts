import { expect, test } from 'vitest';
import type { Activity } from '../../api/activities';
import { seededGoals } from '../../test/seed';
import {
  dayChoicesOf,
  detailsOfActivityDraft,
  draftOfActivity,
  emptyActivityDraft,
  goalChoicesFor,
  withConsistentGoal,
} from './activityDrafts';

const activity: Activity = {
  id: 'activity-survey',
  weekStart: '2026-09-21',
  roleId: 'role-engineer',
  goalId: null,
  title: 'Answer the vendor survey',
  quadrant: 'NOT_IMPORTANT_URGENT',
  scheduledOn: null,
  isCompleted: false,
};

test('plans a new activity as a big rock without a goal or a day', () => {
  expect(emptyActivityDraft('role-saw')).toEqual({
    title: '',
    roleId: 'role-saw',
    goalId: '',
    quadrant: 'IMPORTANT_NOT_URGENT',
    scheduledOn: '',
  });
});

test('edits an activity without goal and day as empty choices and sends them as nothing', () => {
  const draft = draftOfActivity(activity);
  expect(draft).toEqual({
    title: 'Answer the vendor survey',
    roleId: 'role-engineer',
    goalId: '',
    quadrant: 'NOT_IMPORTANT_URGENT',
    scheduledOn: '',
  });
  expect(detailsOfActivityDraft(draft)).toEqual({ ...draft, goalId: null, scheduledOn: null });
});

test('sends a chosen goal and day as they are', () => {
  const draft = { ...draftOfActivity(activity), goalId: 'goal-offline', scheduledOn: '2026-09-22' };
  expect(detailsOfActivityDraft(draft)).toMatchObject({
    goalId: 'goal-offline',
    scheduledOn: '2026-09-22',
  });
  expect(
    draftOfActivity({ ...activity, goalId: 'goal-offline', scheduledOn: '2026-09-22' }),
  ).toMatchObject({
    goalId: 'goal-offline',
    scheduledOn: '2026-09-22',
  });
});

test('forgets a goal that belongs to another role', () => {
  const draft = { ...emptyActivityDraft('role-parent'), goalId: 'goal-offline' };
  expect(withConsistentGoal(draft, seededGoals).goalId).toBe('');
  const matching = { ...emptyActivityDraft('role-engineer'), goalId: 'goal-offline' };
  expect(withConsistentGoal(matching, seededGoals)).toBe(matching);
  const withoutGoal = emptyActivityDraft('role-parent');
  expect(withConsistentGoal(withoutGoal, seededGoals)).toBe(withoutGoal);
});

test('offers no goal and the active goals of the role', () => {
  expect(goalChoicesFor(seededGoals, 'role-saw', null)).toEqual([
    { value: '', label: 'No goal' },
    { value: 'goal-run', label: 'Run a 10K under 55 minutes' },
  ]);
});

test('keeps offering the goal an activity already serves after it was achieved', () => {
  const labels = goalChoicesFor(seededGoals, 'role-saw', 'goal-habits').map(
    (choice) => choice.label,
  );
  expect(labels).toEqual(['No goal', 'Run a 10K under 55 minutes', 'Re-read The 7 Habits']);
});

test('offers no day and every day of the week', () => {
  const choices = dayChoicesOf('2026-09-21');
  expect(choices).toHaveLength(8);
  expect(choices[0]).toEqual({ value: '', label: 'No day' });
  expect(choices[1]).toEqual({ value: '2026-09-21', label: 'Monday, Sep 21' });
  expect(choices[7]).toEqual({ value: '2026-09-27', label: 'Sunday, Sep 27' });
});
