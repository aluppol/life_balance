import { screen, within } from '@testing-library/react';
import { http } from 'msw';
import { expect, test } from 'vitest';
import { problem } from '../../test/handlers/responses';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';
import { server } from '../../test/server';

async function roleNames(): Promise<(string | null)[]> {
  const list = await screen.findByRole('list', { name: 'Roles in order' });
  return within(list)
    .getAllByRole('heading', { level: 3 })
    .map((heading) => heading.textContent);
}

test('marks Sharpen the Saw as built in and offers no way to delete it', async () => {
  renderRoute('/roles');
  expect(await roleNames()).toEqual(['Parent', 'Engineer', 'Sharpen the Saw', 'Friend']);
  const list = screen.getByRole('list', { name: 'Roles in order' });
  const saw = within(list).getAllByRole('listitem')[2];
  expect(saw && within(saw).getByText('Built in')).toBeInTheDocument();
  expect(screen.getAllByText('Built in')).toHaveLength(1);
  expect(screen.queryByRole('button', { name: 'Delete Sharpen the Saw' })).not.toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Edit Sharpen the Saw' })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Delete Parent' })).toBeInTheDocument();
});

test('explains why a role that still has goals cannot be deleted', async () => {
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Delete Engineer' }));
  expect(await screen.findByRole('alert')).toHaveTextContent(
    'The Engineer role still has goals or activities',
  );
  expect(await roleNames()).toEqual(['Parent', 'Engineer', 'Sharpen the Saw', 'Friend']);
});

test('clears an earlier failure when the next change succeeds', async () => {
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Delete Engineer' }));
  await screen.findByRole('alert');
  await user.click(screen.getByRole('button', { name: 'Move Friend up' }));
  await expect.poll(roleNames).toEqual(['Parent', 'Engineer', 'Friend', 'Sharpen the Saw']);
  expect(screen.queryByRole('alert')).not.toBeInTheDocument();
});

test('deletes a role without goals or activities', async () => {
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Delete Friend' }));
  await expect.poll(roleNames).toEqual(['Parent', 'Engineer', 'Sharpen the Saw']);
});

test('adds a role', async () => {
  const { user } = renderRoute('/roles');
  const form = await screen.findByRole('form', { name: 'New role' });
  await user.type(within(form).getByLabelText('Name'), 'Neighbour');
  await user.click(within(form).getByRole('button', { name: 'Add role' }));
  expect(await within(form).findByText('Role added.')).toBeInTheDocument();
  expect(await roleNames()).toEqual([
    'Parent',
    'Engineer',
    'Sharpen the Saw',
    'Friend',
    'Neighbour',
  ]);
  expect(planner().roles.at(-1)).toMatchObject({
    name: 'Neighbour',
    kind: 'PERSONAL',
    position: 4,
  });
});

test('renames the built-in role and keeps it built in', async () => {
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Edit Sharpen the Saw' }));
  const form = screen.getByRole('form', { name: 'Edit Sharpen the Saw' });
  await user.clear(within(form).getByLabelText('Name'));
  await user.type(within(form).getByLabelText('Name'), 'Renewal');
  await user.click(within(form).getByRole('button', { name: 'Save' }));
  expect(await roleNames()).toEqual(['Parent', 'Engineer', 'Renewal', 'Friend']);
  expect(screen.getByText('Built in')).toBeInTheDocument();
});

test('shows the conflict when a renamed role takes the name of another', async () => {
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Edit Friend' }));
  const form = screen.getByRole('form', { name: 'Edit Friend' });
  await user.clear(within(form).getByLabelText('Name'));
  await user.type(within(form).getByLabelText('Name'), 'parent');
  await user.click(within(form).getByRole('button', { name: 'Save' }));
  expect(await within(form).findByRole('alert')).toHaveTextContent(
    "A life role named 'parent' already exists",
  );
});

test('starts a later edit without the failure of an earlier one', async () => {
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Edit Friend' }));
  const form = screen.getByRole('form', { name: 'Edit Friend' });
  await user.clear(within(form).getByLabelText('Name'));
  await user.type(within(form).getByLabelText('Name'), 'Parent');
  await user.click(within(form).getByRole('button', { name: 'Save' }));
  await within(form).findByRole('alert');
  await user.click(within(form).getByRole('button', { name: 'Cancel' }));
  await user.click(screen.getByRole('button', { name: 'Edit Parent' }));
  const nextForm = screen.getByRole('form', { name: 'Edit Parent' });
  expect(within(nextForm).queryByRole('alert')).not.toBeInTheDocument();
});

test('clears a refused reordering when a deletion succeeds', async () => {
  server.use(
    http.put(
      '/api/roles/order',
      () => problem(422, 'The new order must list every item exactly once'),
      {
        once: true,
      },
    ),
  );
  const { user } = renderRoute('/roles');
  await roleNames();
  await user.click(screen.getByRole('button', { name: 'Move Friend up' }));
  expect(await screen.findByRole('alert')).toHaveTextContent(
    'The new order must list every item exactly once',
  );
  await user.click(screen.getByRole('button', { name: 'Delete Friend' }));
  await expect.poll(roleNames).toEqual(['Parent', 'Engineer', 'Sharpen the Saw']);
  expect(screen.queryByRole('alert')).not.toBeInTheDocument();
});
