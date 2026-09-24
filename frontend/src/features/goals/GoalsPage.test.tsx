import { screen, within } from '@testing-library/react';
import { http } from 'msw';
import { expect, test } from 'vitest';
import { problem } from '../../test/handlers/responses';
import { delayNext } from '../../test/delays';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';
import { server } from '../../test/server';

async function goalTitlesOf(roleName: string): Promise<(string | null)[]> {
  const region = await screen.findByRole('region', { name: roleName });
  return within(region)
    .queryAllByRole('heading', { level: 3 })
    .map((heading) => heading.textContent);
}

test('groups the active goals by role with their due dates and values', async () => {
  renderRoute('/goals');
  expect(await goalTitlesOf('Parent')).toEqual(['Teach Mia to ride a bike']);
  const parent = screen.getByRole('region', { name: 'Parent' });
  expect(
    within(parent).getByText('Due October 14, 2026 · Serves Family, Growth'),
  ).toBeInTheDocument();
  expect(within(parent).getByText('No training wheels by her birthday.')).toBeInTheDocument();
  expect(await goalTitlesOf('Engineer')).toEqual(['Ship offline mode']);
  expect(screen.getByText('No due date · Serves Integrity')).toBeInTheDocument();
  expect(await goalTitlesOf('Sharpen the Saw')).toEqual(['Run a 10K under 55 minutes']);
  const friend = screen.getByRole('region', { name: 'Friend' });
  expect(within(friend).getByText('No goals to show for this role.')).toBeInTheDocument();
});

test('filters the goals by status', async () => {
  const { user } = renderRoute('/goals');
  expect(await screen.findByRole('radio', { name: 'Active' })).toBeChecked();
  await user.click(screen.getByRole('radio', { name: 'Achieved' }));
  expect(await goalTitlesOf('Sharpen the Saw')).toEqual(['Re-read The 7 Habits']);
  expect(await goalTitlesOf('Parent')).toEqual([]);
  await user.click(screen.getByRole('radio', { name: 'All' }));
  expect(await goalTitlesOf('Sharpen the Saw')).toEqual([
    'Run a 10K under 55 minutes',
    'Re-read The 7 Habits',
  ]);
  await user.click(screen.getByRole('radio', { name: 'Dropped' }));
  expect(await goalTitlesOf('Sharpen the Saw')).toEqual([]);
});

test('sets a new goal for a role with a due date and the values it serves', async () => {
  const { user } = renderRoute('/goals');
  const form = await screen.findByRole('form', { name: 'New goal' });
  expect(within(form).getByLabelText('Role')).toHaveValue('role-parent');
  await user.selectOptions(within(form).getByLabelText('Role'), 'Engineer');
  await user.type(within(form).getByLabelText('Title'), 'Write the sync tests');
  await user.type(within(form).getByLabelText('Description'), 'Every conflict case.');
  await user.type(within(form).getByLabelText('Due date (optional)'), '2026-10-30');
  expect(within(form).getByRole('checkbox', { name: 'Integrity' })).not.toHaveAttribute(
    'aria-describedby',
  );
  expect(within(form).getByLabelText('Due date (optional)')).toHaveAttribute(
    'aria-invalid',
    'false',
  );
  await user.click(within(form).getByRole('checkbox', { name: 'Integrity' }));
  await user.click(within(form).getByRole('checkbox', { name: 'Growth' }));
  await user.click(within(form).getByRole('button', { name: 'Add goal' }));
  expect(await within(form).findByText('Goal added.')).toBeInTheDocument();
  expect(await goalTitlesOf('Engineer')).toEqual(['Ship offline mode', 'Write the sync tests']);
  expect(screen.getByText('Due October 30, 2026 · Serves Integrity, Growth')).toBeInTheDocument();
  expect(within(form).getByLabelText('Title')).toHaveValue('');
  expect(within(form).getByLabelText('Role')).toHaveValue('role-engineer');
});

test('achieves, reopens and drops a goal', async () => {
  const { user } = renderRoute('/goals');
  await goalTitlesOf('Engineer');
  await user.click(screen.getByRole('button', { name: 'Achieve Ship offline mode' }));
  await expect.poll(() => goalTitlesOf('Engineer')).toEqual([]);
  await user.click(screen.getByRole('radio', { name: 'All' }));
  const engineer = screen.getByRole('region', { name: 'Engineer' });
  expect(await within(engineer).findByText('Achieved')).toBeInTheDocument();
  await user.click(within(engineer).getByRole('button', { name: 'Reopen Ship offline mode' }));
  expect(
    await within(engineer).findByRole('button', { name: 'Drop Ship offline mode' }),
  ).toBeInTheDocument();
  expect(within(engineer).getByText('Active')).toBeInTheDocument();
  await user.click(within(engineer).getByRole('button', { name: 'Drop Ship offline mode' }));
  expect(await within(engineer).findByText('Dropped')).toBeInTheDocument();
  expect(planner().goals.find((goal) => goal.id === 'goal-offline')?.status).toBe('DROPPED');
});

test('edits a goal in place', async () => {
  const { user } = renderRoute('/goals');
  await goalTitlesOf('Parent');
  await user.click(screen.getByRole('button', { name: 'Edit Teach Mia to ride a bike' }));
  const form = screen.getByRole('form', { name: 'Edit Teach Mia to ride a bike' });
  expect(within(form).getByRole('status')).toBeEmptyDOMElement();
  expect(within(form).getByLabelText('Due date (optional)')).toHaveValue('2026-10-14');
  expect(within(form).getByRole('checkbox', { name: 'Family' })).toBeChecked();
  await user.clear(within(form).getByLabelText('Title'));
  await user.type(within(form).getByLabelText('Title'), 'Teach Mia and Leo to ride');
  await user.click(within(form).getByRole('checkbox', { name: 'Family' }));
  await user.click(within(form).getByRole('button', { name: 'Save goal' }));
  expect(await goalTitlesOf('Parent')).toEqual(['Teach Mia and Leo to ride']);
  expect(screen.getByText('Due October 14, 2026 · Serves Growth')).toBeInTheDocument();
});

test('cancels an edit and deletes a goal', async () => {
  const { user } = renderRoute('/goals');
  await goalTitlesOf('Parent');
  await user.click(screen.getByRole('button', { name: 'Edit Teach Mia to ride a bike' }));
  await user.click(screen.getByRole('button', { name: 'Cancel' }));
  expect(
    screen.queryByRole('form', { name: 'Edit Teach Mia to ride a bike' }),
  ).not.toBeInTheDocument();
  await user.click(screen.getByRole('button', { name: 'Delete Teach Mia to ride a bike' }));
  await expect.poll(() => goalTitlesOf('Parent')).toEqual([]);
  expect(
    planner().activities.find((activity) => activity.id === 'activity-bike')?.goalId,
  ).toBeNull();
});

test('shows why a status change was refused', async () => {
  server.use(
    http.put(
      '/api/goals/:id/status',
      () => problem(422, 'Only an active goal can be achieved or dropped'),
      {
        once: true,
      },
    ),
  );
  const { user } = renderRoute('/goals');
  await goalTitlesOf('Parent');
  await user.click(screen.getByRole('button', { name: 'Achieve Teach Mia to ride a bike' }));
  expect(await screen.findByRole('alert')).toHaveTextContent(
    'Only an active goal can be achieved or dropped',
  );
  await user.click(screen.getByRole('button', { name: 'Delete Ship offline mode' }));
  await expect.poll(() => screen.queryByRole('alert')).toBeNull();
});

test('shows the field error for a blank title', async () => {
  const { user } = renderRoute('/goals');
  const form = await screen.findByRole('form', { name: 'New goal' });
  await user.type(within(form).getByLabelText('Title'), '   ');
  await user.click(within(form).getByRole('button', { name: 'Add goal' }));
  expect(await within(form).findByText('must not be blank')).toBeInTheDocument();
  expect(within(form).getByLabelText('Title')).toHaveAccessibleDescription('must not be blank');
});

test('shows a goal that serves no value with its due date only', async () => {
  planner().goals = planner().goals.map((goal) =>
    goal.id === 'goal-bike' ? { ...goal, valueIds: [] } : goal,
  );
  renderRoute('/goals');
  const parent = await screen.findByRole('region', { name: 'Parent' });
  expect(await within(parent).findByText('Due October 14, 2026')).toBeInTheDocument();
});

test('clears a refused deletion when a status change succeeds', async () => {
  server.use(
    http.delete('/api/goals/:id', () => problem(404, 'Goal goal-offline was not found'), {
      once: true,
    }),
  );
  const { user } = renderRoute('/goals');
  await goalTitlesOf('Engineer');
  await user.click(screen.getByRole('button', { name: 'Delete Ship offline mode' }));
  expect(await screen.findByRole('alert')).toHaveTextContent('Goal goal-offline was not found');
  await user.click(screen.getByRole('button', { name: 'Achieve Ship offline mode' }));
  await expect.poll(() => goalTitlesOf('Engineer')).toEqual([]);
  expect(screen.queryByRole('alert')).not.toBeInTheDocument();
});

test('holds the goal actions while a status change is being saved', async () => {
  delayNext('put', '/api/goals/:id/status');
  const { user } = renderRoute('/goals');
  await goalTitlesOf('Engineer');
  await user.click(screen.getByRole('button', { name: 'Achieve Ship offline mode' }));
  expect(screen.getByRole('button', { name: 'Drop Teach Mia to ride a bike' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Delete Teach Mia to ride a bike' })).toBeDisabled();
  await expect.poll(() => goalTitlesOf('Engineer')).toEqual([]);
  expect(screen.getByRole('button', { name: 'Drop Teach Mia to ride a bike' })).toBeEnabled();
});

test('shows the description of a new goal in the list of its role', async () => {
  const { user } = renderRoute('/goals');
  const form = await screen.findByRole('form', { name: 'New goal' });
  const values = within(form).getByRole('group', { name: 'Values it serves' });
  expect(within(values).queryAllByRole('paragraph')).toHaveLength(0);
  await user.type(within(form).getByLabelText('Title'), 'Build the tree house');
  await user.type(within(form).getByLabelText('Description'), 'Before the summer holidays.');
  await user.click(within(form).getByRole('button', { name: 'Add goal' }));
  const parent = screen.getByRole('list', { name: 'Goals of Parent' });
  expect(await within(parent).findByText('Before the summer holidays.')).toBeInTheDocument();
});

test('shows only the facts of a goal without a description', async () => {
  renderRoute('/goals');
  const engineer = await screen.findByRole('list', { name: 'Goals of Engineer' });
  const paragraphs = within(engineer).getAllByRole('paragraph');
  expect(paragraphs.map((paragraph) => paragraph.textContent)).toEqual([
    'No due date · Serves Integrity',
  ]);
});

test('shows the dropped goals under the Dropped filter', async () => {
  planner().goals = planner().goals.map((goal) =>
    goal.id === 'goal-offline' ? { ...goal, status: 'DROPPED' } : goal,
  );
  const { user } = renderRoute('/goals');
  await user.click(await screen.findByRole('radio', { name: 'Dropped' }));
  expect(await goalTitlesOf('Engineer')).toEqual(['Ship offline mode']);
  expect(await goalTitlesOf('Parent')).toEqual([]);
});
