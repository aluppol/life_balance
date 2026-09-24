import { screen, waitFor, within } from '@testing-library/react';
import { http } from 'msw';
import { expect, test } from 'vitest';
import { problem } from '../../test/handlers/responses';
import { delayNext } from '../../test/delays';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';
import { countLoads } from '../../test/requests';
import { server } from '../../test/server';

async function rankedNames(): Promise<(string | null)[]> {
  const list = await screen.findByRole('list', { name: 'Core values in rank order' });
  return within(list)
    .getAllByRole('heading', { level: 3 })
    .map((heading) => heading.textContent);
}

test('lists the core values in rank order with their descriptions', async () => {
  renderRoute('/values');
  expect(await rankedNames()).toEqual(['Integrity', 'Family', 'Growth', 'Health']);
  expect(screen.getByText('Keep promises, especially the small ones.')).toBeInTheDocument();
  expect(screen.getByRole('group', { name: 'Rank of Integrity' })).toBeInTheDocument();
  expect(document.title).toBe('Core values · Life Balance');
});

test('adds a value, confirms it and clears the form', async () => {
  const { user } = renderRoute('/values');
  const form = await screen.findByRole('form', { name: 'New value' });
  await user.type(within(form).getByLabelText('Name'), 'Courage');
  await user.type(within(form).getByLabelText('Description'), 'Do the hard thing first.');
  await user.click(within(form).getByRole('button', { name: 'Add value' }));
  expect(await within(form).findByText('Value added.')).toBeInTheDocument();
  expect(await rankedNames()).toEqual(['Integrity', 'Family', 'Growth', 'Health', 'Courage']);
  expect(screen.getByText('Do the hard thing first.')).toBeInTheDocument();
  expect(within(form).getByLabelText('Name')).toHaveValue('');
  expect(within(form).getByLabelText('Description')).toHaveValue('');
});

test('shows the conflict when another value already has the name', async () => {
  const { user } = renderRoute('/values');
  const form = await screen.findByRole('form', { name: 'New value' });
  await user.type(within(form).getByLabelText('Name'), 'integrity');
  await user.click(within(form).getByRole('button', { name: 'Add value' }));
  expect(await within(form).findByRole('alert')).toHaveTextContent(
    "A core value named 'integrity' already exists",
  );
  expect(within(form).getByLabelText('Name')).toHaveValue('integrity');
});

test('shows the field error next to a blank name', async () => {
  const { user } = renderRoute('/values');
  const form = await screen.findByRole('form', { name: 'New value' });
  await user.type(within(form).getByLabelText('Name'), '   ');
  await user.click(within(form).getByRole('button', { name: 'Add value' }));
  const name = within(form).getByLabelText('Name');
  expect(await within(form).findByText('must not be blank')).toBeInTheDocument();
  expect(name).toHaveAttribute('aria-invalid', 'true');
  expect(name).toHaveAccessibleDescription('must not be blank');
  expect(within(form).getByRole('alert')).toHaveTextContent('Some fields are invalid');
  expect(within(form).getByLabelText('Description')).toHaveAttribute('aria-invalid', 'false');
});

test('moves values up and down and sends the full order', async () => {
  const { user } = renderRoute('/values');
  await rankedNames();
  expect(screen.getByRole('button', { name: 'Move Integrity up' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Move Health down' })).toBeDisabled();
  await user.click(screen.getByRole('button', { name: 'Move Family up' }));
  await expect.poll(rankedNames).toEqual(['Family', 'Integrity', 'Growth', 'Health']);
  await user.click(screen.getByRole('button', { name: 'Move Integrity down' }));
  await expect
    .poll(() => planner().values.map((value) => value.name))
    .toEqual(['Family', 'Growth', 'Integrity', 'Health']);
  expect(await rankedNames()).toEqual(['Family', 'Growth', 'Integrity', 'Health']);
});

test('edits a value in place', async () => {
  const { user } = renderRoute('/values');
  await rankedNames();
  await user.click(screen.getByRole('button', { name: 'Edit Growth' }));
  const form = screen.getByRole('form', { name: 'Edit Growth' });
  expect(within(form).getByRole('status')).toBeEmptyDOMElement();
  expect(within(form).getByLabelText('Name')).toHaveValue('Growth');
  await user.clear(within(form).getByLabelText('Name'));
  await user.type(within(form).getByLabelText('Name'), 'Learning');
  await user.click(within(form).getByRole('button', { name: 'Save' }));
  expect(await rankedNames()).toEqual(['Integrity', 'Family', 'Learning', 'Health']);
  expect(screen.queryByRole('form', { name: 'Edit Growth' })).not.toBeInTheDocument();
});

test('cancels an edit without saving', async () => {
  const { user } = renderRoute('/values');
  await rankedNames();
  await user.click(screen.getByRole('button', { name: 'Edit Family' }));
  const form = screen.getByRole('form', { name: 'Edit Family' });
  await user.type(within(form).getByLabelText('Name'), ' time');
  await user.click(within(form).getByRole('button', { name: 'Cancel' }));
  expect(await rankedNames()).toEqual(['Integrity', 'Family', 'Growth', 'Health']);
  expect(planner().values[1]?.name).toBe('Family');
});

test('deletes a value', async () => {
  const { user } = renderRoute('/values');
  await rankedNames();
  await user.click(screen.getByRole('button', { name: 'Delete Health' }));
  await expect.poll(rankedNames).toEqual(['Integrity', 'Family', 'Growth']);
});

test('reports a failed load and loads again on request', async () => {
  server.use(
    http.get('/api/values', () => problem(500, 'The values are unavailable'), { once: true }),
  );
  const { user } = renderRoute('/values');
  expect(await screen.findByText('The values are unavailable')).toBeInTheDocument();
  await user.click(screen.getByRole('button', { name: 'Try again' }));
  expect(await rankedNames()).toEqual(['Integrity', 'Family', 'Growth', 'Health']);
});

test('invites adding the first value when there are none', async () => {
  planner().values = [];
  renderRoute('/values');
  expect(await screen.findByText('No values yet. Add the first one below.')).toBeInTheDocument();
  expect(screen.queryByRole('list', { name: 'Core values in rank order' })).not.toBeInTheDocument();
});

test('shows that the values are loading', async () => {
  delayNext('get', '/api/values');
  renderRoute('/values');
  expect(await screen.findByText('Loading…')).toBeInTheDocument();
  expect(await rankedNames()).toEqual(['Integrity', 'Family', 'Growth', 'Health']);
  expect(screen.queryByText('Loading…')).not.toBeInTheDocument();
});

test('holds every move and deletion while a new order is being saved', async () => {
  delayNext('put', '/api/values/order');
  const { user } = renderRoute('/values');
  await rankedNames();
  await user.click(screen.getByRole('button', { name: 'Move Family up' }));
  expect(screen.getByRole('button', { name: 'Move Growth up' })).toBeDisabled();
  expect(screen.getByRole('button', { name: 'Delete Growth' })).toBeDisabled();
  await waitFor(() => {
    expect(screen.getByRole('button', { name: 'Move Growth up' })).toBeEnabled();
  });
  expect(await rankedNames()).toEqual(['Family', 'Integrity', 'Growth', 'Health']);
});

test('holds the form while a new value is being saved', async () => {
  delayNext('post', '/api/values');
  const { user } = renderRoute('/values');
  const form = await screen.findByRole('form', { name: 'New value' });
  expect(within(form).getByRole('status')).toBeEmptyDOMElement();
  await user.type(within(form).getByLabelText('Name'), 'Courage');
  await user.click(within(form).getByRole('button', { name: 'Add value' }));
  expect(within(form).getByRole('button', { name: 'Add value' })).toBeDisabled();
  expect(await within(form).findByText('Value added.')).toBeInTheDocument();
  expect(within(form).getByRole('button', { name: 'Add value' })).toBeEnabled();
});

test('shows a description only for the values that have one', async () => {
  renderRoute('/values');
  const list = await screen.findByRole('list', { name: 'Core values in rank order' });
  const [integrity, , , health] = within(list).getAllByRole('listitem');
  expect(integrity && within(integrity).getByRole('paragraph')).toHaveTextContent(
    'Keep promises, especially the small ones.',
  );
  expect(health && within(health).queryByRole('paragraph')).toBeNull();
});

test('refreshes only the values after adding one', async () => {
  const valueLoads = countLoads('/api/values');
  const accountLoads = countLoads('/api/me');
  const { user } = renderRoute('/values');
  const form = await screen.findByRole('form', { name: 'New value' });
  await user.type(within(form).getByLabelText('Name'), 'Courage');
  await user.click(within(form).getByRole('button', { name: 'Add value' }));
  expect(await within(form).findByText('Value added.')).toBeInTheDocument();
  expect(valueLoads()).toBe(2);
  expect(accountLoads()).toBe(1);
});
