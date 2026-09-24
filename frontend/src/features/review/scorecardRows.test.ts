import { expect, test } from 'vitest';
import type { WeekScorecard } from '../../api/scorecard';
import { seededRoles } from '../../test/seed';
import { quadrantRows, roleRows } from './scorecardRows';

const scorecard: WeekScorecard = {
  weekStart: '2026-09-21',
  overall: { planned: 6, completed: 3 },
  bigRocks: { planned: 3, completed: 1 },
  byQuadrant: {
    IMPORTANT_URGENT: { planned: 1, completed: 1 },
    IMPORTANT_NOT_URGENT: { planned: 3, completed: 1 },
    NOT_IMPORTANT_URGENT: { planned: 2, completed: 1 },
    NOT_IMPORTANT_NOT_URGENT: { planned: 0, completed: 0 },
  },
  byRole: [
    { roleId: 'role-engineer', planned: 2, completed: 1 },
    { roleId: 'role-gone', planned: 1, completed: 0 },
    { roleId: 'role-parent', planned: 1, completed: 0 },
    { roleId: 'role-saw', planned: 2, completed: 2 },
  ],
};

test('lists the quadrants in Covey order with their tallies', () => {
  expect(quadrantRows(scorecard)).toEqual([
    { key: 'IMPORTANT_URGENT', label: 'Q1 · Important and urgent', planned: 1, completed: 1 },
    { key: 'IMPORTANT_NOT_URGENT', label: 'Q2 · Important, not urgent', planned: 3, completed: 1 },
    { key: 'NOT_IMPORTANT_URGENT', label: 'Q3 · Urgent, not important', planned: 2, completed: 1 },
    {
      key: 'NOT_IMPORTANT_NOT_URGENT',
      label: 'Q4 · Neither urgent nor important',
      planned: 0,
      completed: 0,
    },
  ]);
});

test('lists the roles in role order with unknown roles last', () => {
  expect(
    roleRows(scorecard, seededRoles).map((row) => [row.label, row.planned, row.completed]),
  ).toEqual([
    ['Parent', 1, 0],
    ['Engineer', 2, 1],
    ['Sharpen the Saw', 2, 2],
    ['Unknown role', 1, 0],
  ]);
  expect(roleRows(scorecard, seededRoles)[0]?.key).toBe('role-parent');
});
