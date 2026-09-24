import { screen, within } from '@testing-library/react';
import { expect, test } from 'vitest';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';

test('shows the mission and the values in rank order', async () => {
  renderRoute('/');
  expect(
    await screen.findByText('I live by principles I choose, not by moods I happen to have.'),
  ).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Edit your mission' })).toHaveAttribute(
    'href',
    '/mission',
  );
  const values = await screen.findByRole('list', { name: 'Values in rank order' });
  expect(
    within(values)
      .getAllByRole('listitem')
      .map((value) => value.textContent),
  ).toEqual(['Integrity', 'Family', 'Growth', 'Health']);
});

test('shows every role with its number of active goals', async () => {
  renderRoute('/');
  const roles = await screen.findByRole('list', { name: 'Roles and their active goals' });
  const summaries = within(roles)
    .getAllByRole('listitem')
    .map((role) => Array.from(role.children, (part) => part.textContent));
  expect(summaries).toEqual([
    ['Parent', '1 active goal'],
    ['Engineer', '1 active goal'],
    ['Sharpen the Saw', '1 active goal'],
    ['Friend', 'No active goals'],
  ]);
});

test('shows how many big rocks of this week are done', async () => {
  renderRoute('/');
  expect(
    await screen.findByRole('progressbar', { name: 'Big rocks: 1 of 3 done' }),
  ).toHaveAttribute('max', '3');
  expect(screen.getByRole('progressbar', { name: 'All activities: 2 of 5 done' })).toHaveAttribute(
    'value',
    '2',
  );
});

test('links to planning this week and reviewing last week', () => {
  renderRoute('/');
  const nextSteps = screen.getByRole('navigation', { name: 'Next steps' });
  expect(within(nextSteps).getByRole('link', { name: 'Plan this week' })).toHaveAttribute(
    'href',
    '/week/2026-09-21',
  );
  expect(within(nextSteps).getByRole('link', { name: 'Review last week' })).toHaveAttribute(
    'href',
    '/review/2026-09-14',
  );
});

test('invites a new person to write a mission, name values and plan a week', async () => {
  planner().mission = null;
  planner().values = [];
  planner().activities = [];
  renderRoute('/');
  expect(await screen.findByRole('link', { name: 'Write your mission' })).toHaveAttribute(
    'href',
    '/mission',
  );
  expect(await screen.findByRole('link', { name: 'Name your values' })).toHaveAttribute(
    'href',
    '/values',
  );
  expect(await screen.findByText('Big rocks: none planned')).toBeInTheDocument();
  expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
});
