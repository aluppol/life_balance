import { expect, test } from 'vitest';
import { seededActivities, seededGoals, seededRoles, thisMonday } from '../../test/seed';
import {
  activitiesInQuadrant,
  activitiesOn,
  purposeOf,
  quadrantOf,
  scheduleOf,
  unscheduledActivities,
} from './activityFacts';

const thisWeek = seededActivities.filter((activity) => activity.weekStart === thisMonday);

function titles(activities: readonly { readonly title: string }[]): string[] {
  return activities.map((activity) => activity.title);
}

test('picks the activities of a quadrant', () => {
  expect(titles(activitiesInQuadrant(thisWeek, 'IMPORTANT_NOT_URGENT'))).toEqual([
    'Weekly planning',
    'Tempo run, 5K',
    'Bike practice in the park',
  ]);
  expect(activitiesInQuadrant(thisWeek, 'NOT_IMPORTANT_NOT_URGENT')).toEqual([]);
});

test('picks the activities of a day and the unscheduled ones', () => {
  expect(titles(activitiesOn(thisWeek, '2026-09-21'))).toEqual([
    'Weekly planning',
    'Fix the checkout outage',
  ]);
  expect(titles(unscheduledActivities(thisWeek))).toEqual(['Answer the vendor survey']);
});

test('describes the role and the goal an activity serves', () => {
  const [planning, , tempo] = thisWeek;
  expect(planning && purposeOf(planning, seededRoles, seededGoals)).toBe('Sharpen the Saw');
  expect(tempo && purposeOf(tempo, seededRoles, seededGoals)).toBe(
    'Sharpen the Saw · Run a 10K under 55 minutes',
  );
  expect(tempo && purposeOf({ ...tempo, roleId: 'role-gone' }, seededRoles, [])).toBe(
    'Unknown role',
  );
});

test('describes when and in which quadrant an activity happens', () => {
  const [planning, , , , survey] = thisWeek;
  expect(planning && scheduleOf(planning)).toBe('Monday');
  expect(survey && scheduleOf(survey)).toBe('No day');
  expect(survey && quadrantOf(survey)).toBe('Q3 · Urgent, not important');
});
