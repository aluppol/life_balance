import { screen, waitFor, within } from '@testing-library/react';
import { expect, test } from 'vitest';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';

test("shows today's activities with their role, goal and quadrant", async () => {
  renderRoute('/day');
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
    'Wednesday, September 23, 2026',
  );
  const list = await screen.findByRole('list', { name: 'Activities of the day' });
  expect(within(list).getByRole('checkbox', { name: 'Tempo run, 5K' })).not.toBeChecked();
  expect(
    within(list).getByText(
      'Sharpen the Saw · Run a 10K under 55 minutes · Q2 · Important, not urgent',
    ),
  ).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Open the week of September 21, 2026' })).toHaveAttribute(
    'href',
    '/week/2026-09-21',
  );
  expect(screen.getByRole('link', { name: 'Today' })).toHaveAttribute('aria-current', 'page');
});

test('ticks an activity done from the day', async () => {
  const { user } = renderRoute('/day/2026-09-23');
  await user.click(await screen.findByRole('checkbox', { name: 'Tempo run, 5K' }));
  await waitFor(() => {
    expect(screen.getByRole('checkbox', { name: 'Tempo run, 5K' })).toBeChecked();
  });
  expect(
    planner().activities.find((activity) => activity.id === 'activity-tempo')?.isCompleted,
  ).toBe(true);
});

test('says when nothing is scheduled for the day', async () => {
  renderRoute('/day/2026-09-22');
  expect(await screen.findByText('Nothing is scheduled for this day.')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Today' })).not.toHaveAttribute('aria-current');
});

test('moves to the next and the previous day and back to today', async () => {
  const { user } = renderRoute('/day');
  await user.click(screen.getByRole('link', { name: 'Next day' }));
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
    'Thursday, September 24, 2026',
  );
  await user.click(screen.getByRole('link', { name: 'Previous day' }));
  await user.click(screen.getByRole('link', { name: 'Previous day' }));
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
    'Tuesday, September 22, 2026',
  );
  await user.click(screen.getByRole('link', { name: 'Today' }));
  expect(await screen.findByRole('checkbox', { name: 'Tempo run, 5K' })).toBeInTheDocument();
});

test('shows a day of another week with a link to that week', async () => {
  renderRoute('/day/2026-09-15');
  expect(await screen.findByRole('checkbox', { name: 'Write the design doc' })).toBeChecked();
  expect(screen.getByRole('link', { name: 'Open the week of September 14, 2026' })).toHaveAttribute(
    'href',
    '/week/2026-09-14',
  );
});

test('answers not found for a day that is not a date', () => {
  renderRoute('/day/2026-02-30');
  expect(screen.getByRole('heading', { level: 1, name: 'Page not found' })).toBeInTheDocument();
});
