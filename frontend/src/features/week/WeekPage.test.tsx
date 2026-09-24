import { screen, waitFor, within } from '@testing-library/react';
import { http } from 'msw';
import { expect, test } from 'vitest';
import { problem } from '../../test/handlers/responses';
import { delayNext } from '../../test/delays';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';
import { server } from '../../test/server';

async function checkboxLabels(listName: string): Promise<string[]> {
  const list = await screen.findByRole('list', { name: listName });
  return within(list)
    .getAllByRole('checkbox')
    .map((checkbox) => checkbox.getAttribute('id') ?? '')
    .map((id) => document.querySelector(`label[for="${id}"]`)?.textContent ?? '');
}

function glanceAt(dayName: string): HTMLElement {
  return screen.getByRole('region', { name: dayName });
}

test('opens this week with the big rocks first', async () => {
  renderRoute('/week');
  expect(await screen.findByRole('heading', { level: 1 })).toHaveTextContent(
    'Week of September 21, 2026',
  );
  expect(await checkboxLabels('Big rocks')).toEqual([
    'Weekly planning',
    'Tempo run, 5K',
    'Bike practice in the park',
  ]);
  expect(screen.getByRole('checkbox', { name: 'Weekly planning' })).toBeChecked();
  expect(screen.getByRole('checkbox', { name: 'Tempo run, 5K' })).not.toBeChecked();
  expect(
    screen.getByText('Sharpen the Saw · Run a 10K under 55 minutes · Wednesday'),
  ).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'This week' })).toHaveAttribute('aria-current', 'page');
});

test('lists the other quadrants after the big rocks', async () => {
  renderRoute('/week/2026-09-21');
  expect(await checkboxLabels('Q1 · Important and urgent')).toEqual(['Fix the checkout outage']);
  expect(await checkboxLabels('Q3 · Urgent, not important')).toEqual(['Answer the vendor survey']);
  const neither = screen.getByRole('region', { name: 'Q4 · Neither urgent nor important' });
  expect(within(neither).getByText('Nothing planned here.')).toBeInTheDocument();
  expect(screen.getByText('Engineer · No day')).toBeInTheDocument();
});

test('shows the week at a glance with the unscheduled activities', async () => {
  renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  const monday = glanceAt('Mon, Sep 21');
  expect(
    within(monday)
      .getAllByRole('listitem')
      .map((entry) => entry.textContent),
  ).toEqual(['Weekly planning Done', 'Fix the checkout outage Done']);
  expect(within(glanceAt('Wed, Sep 23')).getByText('Tempo run, 5K')).toBeInTheDocument();
  expect(within(glanceAt('Tue, Sep 22')).getByText('Nothing planned')).toBeInTheDocument();
  expect(within(glanceAt('Unscheduled')).getByText('Answer the vendor survey')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Sat, Sep 26' })).toHaveAttribute(
    'href',
    '/day/2026-09-26',
  );
});

test('moves to the next, the previous and back to this week', async () => {
  const { user } = renderRoute('/week');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('link', { name: 'Next week' }));
  expect(
    await screen.findByText('No big rocks planned yet. Start with one for each role.'),
  ).toBeInTheDocument();
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Week of September 28, 2026');
  expect(screen.getByRole('link', { name: 'This week' })).not.toHaveAttribute('aria-current');
  await user.click(screen.getByRole('link', { name: 'Previous week' }));
  await user.click(screen.getByRole('link', { name: 'Previous week' }));
  expect(await checkboxLabels('Big rocks')).toEqual(['Write the design doc', 'Long run, 7K']);
  await user.click(screen.getByRole('link', { name: 'This week' }));
  expect(await checkboxLabels('Big rocks')).toContain('Tempo run, 5K');
});

test('plans a big rock for a goal of the chosen role', async () => {
  const { user } = renderRoute('/week/2026-09-21');
  const form = await screen.findByRole('form', { name: 'New activity' });
  expect(within(form).getByLabelText('Quadrant')).toHaveValue('IMPORTANT_NOT_URGENT');
  await user.type(within(form).getByLabelText('Title'), 'Balance practice');
  await user.selectOptions(within(form).getByLabelText('Goal'), 'Teach Mia to ride a bike');
  await user.selectOptions(within(form).getByLabelText('Day'), 'Thursday, Sep 24');
  await user.click(within(form).getByRole('button', { name: 'Plan activity' }));
  expect(await within(form).findByText('Activity planned.')).toBeInTheDocument();
  expect(await checkboxLabels('Big rocks')).toContain('Balance practice');
  expect(within(glanceAt('Thu, Sep 24')).getByText('Balance practice')).toBeInTheDocument();
  expect(screen.getByText('Parent · Teach Mia to ride a bike · Thursday')).toBeInTheDocument();
  expect(within(form).getByLabelText('Title')).toHaveValue('');
  expect(within(form).getByLabelText('Role')).toHaveValue('role-parent');
});

test('offers only the active goals of the chosen role and forgets a goal of another role', async () => {
  const { user } = renderRoute('/week/2026-09-21');
  const form = await screen.findByRole('form', { name: 'New activity' });
  await user.selectOptions(within(form).getByLabelText('Role'), 'Sharpen the Saw');
  const goal = within(form).getByLabelText('Goal');
  expect(
    within(goal)
      .getAllByRole('option')
      .map((option) => option.textContent),
  ).toEqual(['No goal', 'Run a 10K under 55 minutes']);
  await user.selectOptions(goal, 'Run a 10K under 55 minutes');
  await user.selectOptions(within(form).getByLabelText('Role'), 'Engineer');
  expect(within(form).getByLabelText('Goal')).toHaveValue('');
  await user.selectOptions(
    within(form).getByLabelText('Quadrant'),
    'Q4 · Neither urgent nor important',
  );
  await user.type(within(form).getByLabelText('Title'), 'Tidy the inbox');
  await user.click(within(form).getByRole('button', { name: 'Plan activity' }));
  expect(await checkboxLabels('Q4 · Neither urgent nor important')).toEqual(['Tidy the inbox']);
});

test('ticks an activity done and undone', async () => {
  const { user } = renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('checkbox', { name: 'Tempo run, 5K' }));
  await waitFor(() => {
    expect(screen.getByRole('checkbox', { name: 'Tempo run, 5K' })).toBeChecked();
  });
  expect(within(glanceAt('Wed, Sep 23')).getByText('Done')).toBeInTheDocument();
  await user.click(screen.getByRole('checkbox', { name: 'Tempo run, 5K' }));
  await waitFor(() => {
    expect(screen.getByRole('checkbox', { name: 'Tempo run, 5K' })).not.toBeChecked();
  });
  expect(
    planner().activities.find((activity) => activity.id === 'activity-tempo')?.isCompleted,
  ).toBe(false);
});

test('moves an unscheduled activity to a day', async () => {
  const { user } = renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('button', { name: 'Edit Answer the vendor survey' }));
  const form = screen.getByRole('form', { name: 'Edit Answer the vendor survey' });
  expect(within(form).getByRole('status')).toBeEmptyDOMElement();
  expect(within(form).getByLabelText('Day')).toHaveValue('');
  await user.selectOptions(within(form).getByLabelText('Day'), 'Friday, Sep 25');
  await user.click(within(form).getByRole('button', { name: 'Save activity' }));
  expect(
    await within(glanceAt('Fri, Sep 25')).findByText('Answer the vendor survey'),
  ).toBeInTheDocument();
  expect(within(glanceAt('Unscheduled')).getByText('Nothing planned')).toBeInTheDocument();
  expect(
    screen.queryByRole('form', { name: 'Edit Answer the vendor survey' }),
  ).not.toBeInTheDocument();
});

test('keeps the achieved goal an activity serves when editing it', async () => {
  planner().activities = planner().activities.map((activity) =>
    activity.id === 'activity-planning' ? { ...activity, goalId: 'goal-habits' } : activity,
  );
  const { user } = renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('button', { name: 'Edit Weekly planning' }));
  const form = screen.getByRole('form', { name: 'Edit Weekly planning' });
  expect(within(form).getByLabelText('Goal')).toHaveValue('goal-habits');
  await user.click(within(form).getByRole('button', { name: 'Cancel' }));
  expect(screen.getByText('Sharpen the Saw · Re-read The 7 Habits · Monday')).toBeInTheDocument();
});

test('deletes an activity', async () => {
  const { user } = renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('button', { name: 'Delete Bike practice in the park' }));
  await expect
    .poll(() => checkboxLabels('Big rocks'))
    .toEqual(['Weekly planning', 'Tempo run, 5K']);
});

test('shows why the server refused an activity', async () => {
  server.use(
    http.post('/api/weeks/:monday/activities', () =>
      problem(422, '2026-09-30 is outside the week starting 2026-09-21'),
    ),
  );
  const { user } = renderRoute('/week/2026-09-21');
  const form = await screen.findByRole('form', { name: 'New activity' });
  await user.type(within(form).getByLabelText('Title'), 'Too late');
  await user.click(within(form).getByRole('button', { name: 'Plan activity' }));
  expect(await within(form).findByRole('alert')).toHaveTextContent(
    '2026-09-30 is outside the week starting 2026-09-21',
  );
});

test('shows why the server refused a completion until another change succeeds', async () => {
  server.use(
    http.put('/api/activities/:id/completion', () => problem(404, 'Activity was not found'), {
      once: true,
    }),
  );
  const { user } = renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('checkbox', { name: 'Tempo run, 5K' }));
  expect(await screen.findByRole('alert')).toHaveTextContent('Activity was not found');
  await user.click(screen.getByRole('button', { name: 'Delete Bike practice in the park' }));
  await expect
    .poll(() => checkboxLabels('Big rocks'))
    .toEqual(['Weekly planning', 'Tempo run, 5K']);
  expect(screen.queryByRole('alert')).not.toBeInTheDocument();
});

test('holds the check-offs and deletions while a completion is being saved', async () => {
  delayNext('put', '/api/activities/:id/completion');
  const { user } = renderRoute('/week/2026-09-21');
  await checkboxLabels('Big rocks');
  await user.click(screen.getByRole('checkbox', { name: 'Tempo run, 5K' }));
  expect(screen.getByRole('checkbox', { name: 'Bike practice in the park' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Delete Weekly planning' })).toBeDisabled();
  await waitFor(() => {
    expect(screen.getByRole('checkbox', { name: 'Tempo run, 5K' })).toBeChecked();
  });
  expect(screen.getByRole('checkbox', { name: 'Bike practice in the park' })).toBeEnabled();
});

test('opens the Monday of a week given by another day', async () => {
  const { router } = renderRoute('/week/2026-09-24');
  expect(await screen.findByRole('heading', { level: 1 })).toHaveTextContent(
    'Week of September 21, 2026',
  );
  expect(router.state.location.pathname).toBe('/week/2026-09-21');
});

test('answers not found for a week that is not a date', () => {
  renderRoute('/week/someday');
  expect(screen.getByRole('heading', { level: 1, name: 'Page not found' })).toBeInTheDocument();
});
