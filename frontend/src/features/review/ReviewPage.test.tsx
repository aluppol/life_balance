import { screen, within } from '@testing-library/react';
import { expect, test } from 'vitest';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';

function tableRows(caption: string): string[][] {
  const table = screen.getByRole('table', { name: caption });
  return within(table)
    .getAllByRole('row')
    .map((row) => Array.from(row.children, (cell) => cell.textContent));
}

test('reviews last week by default with its scorecard', async () => {
  renderRoute('/review');
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
    'Review of the week of September 14, 2026',
  );
  expect(await screen.findByText('All activities: 2 of 3 done')).toBeInTheDocument();
  expect(screen.getByText('Big rocks: 1 of 2 done')).toBeInTheDocument();
  expect(tableRows('By quadrant')).toEqual([
    ['Quadrant', 'Planned', 'Done'],
    ['Q1 · Important and urgent', '0', '0'],
    ['Q2 · Important, not urgent', '2', '1'],
    ['Q3 · Urgent, not important', '1', '1'],
    ['Q4 · Neither urgent nor important', '0', '0'],
  ]);
  expect(screen.getByRole('link', { name: 'Last week' })).toHaveAttribute('aria-current', 'page');
});

test('names the roles of the scorecard in role order', async () => {
  renderRoute('/review/2026-09-21');
  await screen.findByText('All activities: 2 of 5 done');
  expect(tableRows('By role')).toEqual([
    ['Role', 'Planned', 'Done'],
    ['Parent', '1', '0'],
    ['Engineer', '2', '1'],
    ['Sharpen the Saw', '2', '1'],
  ]);
});

test('shows the written review with the renewed dimensions', async () => {
  renderRoute('/review/2026-09-14');
  expect(await screen.findByLabelText('What did you accomplish?')).toHaveValue(
    'Finished the design doc.',
  );
  expect(screen.getByLabelText('What did you learn?')).toHaveValue('Status meetings ate Thursday.');
  expect(screen.getByRole('checkbox', { name: 'Physical' })).toBeChecked();
  expect(screen.getByRole('checkbox', { name: 'Physical' })).toHaveAccessibleDescription(
    'Exercise, nutrition, rest.',
  );
  expect(screen.getByRole('checkbox', { name: 'Mental' })).toBeChecked();
  expect(screen.getByRole('checkbox', { name: 'Mental' })).toHaveAccessibleDescription(
    'Reading, learning, planning.',
  );
  expect(screen.getByRole('checkbox', { name: 'Social/Emotional' })).toHaveAccessibleDescription(
    'Service, empathy, connection.',
  );
  expect(screen.getByRole('checkbox', { name: 'Social/Emotional' })).not.toBeChecked();
  expect(screen.getByRole('checkbox', { name: 'Spiritual' })).toHaveAccessibleDescription(
    'Values, meditation, nature.',
  );
});

test('writes the review of a week that has none yet', async () => {
  const { user } = renderRoute('/review/2026-09-21');
  const accomplishments = await screen.findByLabelText('What did you accomplish?');
  expect(accomplishments).toHaveValue('');
  await user.type(accomplishments, 'Two runs and a bike lesson.');
  await user.type(screen.getByLabelText('What did you learn?'), 'Mornings work best.');
  await user.click(screen.getByRole('checkbox', { name: 'Spiritual' }));
  await user.click(screen.getByRole('checkbox', { name: 'Physical' }));
  await user.click(screen.getByRole('button', { name: 'Save review' }));
  expect(await screen.findByText('Review saved.')).toBeInTheDocument();
  expect(planner().reviews.find((review) => review.weekStart === '2026-09-21')).toEqual({
    weekStart: '2026-09-21',
    accomplishments: 'Two runs and a bike lesson.',
    lessons: 'Mornings work best.',
    renewedDimensions: ['PHYSICAL', 'SPIRITUAL'],
  });
});

test('changes the renewed dimensions of a written review', async () => {
  const { user } = renderRoute('/review');
  await user.click(await screen.findByRole('checkbox', { name: 'Mental' }));
  await user.click(screen.getByRole('checkbox', { name: 'Social/Emotional' }));
  await user.click(screen.getByRole('button', { name: 'Save review' }));
  expect(await screen.findByText('Review saved.')).toBeInTheDocument();
  expect(
    planner().reviews.find((review) => review.weekStart === '2026-09-14')?.renewedDimensions,
  ).toEqual(['PHYSICAL', 'SOCIAL_EMOTIONAL']);
});

test('says when nothing was planned and moves between weeks', async () => {
  const { user } = renderRoute('/review');
  await screen.findByText('All activities: 2 of 3 done');
  await user.click(screen.getByRole('link', { name: 'Previous week' }));
  expect(await screen.findByText('Nothing was planned for this week.')).toBeInTheDocument();
  expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
    'Review of the week of September 7, 2026',
  );
  await user.click(screen.getByRole('link', { name: 'Next week' }));
  await user.click(screen.getByRole('link', { name: 'Next week' }));
  expect(await screen.findByText('All activities: 2 of 5 done')).toBeInTheDocument();
});

test('opens the review of the Monday of a week given by another day', async () => {
  const { router } = renderRoute('/review/2026-09-17');
  expect(await screen.findByText('All activities: 2 of 3 done')).toBeInTheDocument();
  expect(router.state.location.pathname).toBe('/review/2026-09-14');
});

test('saves a review without renewed dimensions', async () => {
  const { user } = renderRoute('/review/2026-09-21');
  await user.type(await screen.findByLabelText('What did you learn?'), 'Rest matters.');
  await user.click(screen.getByRole('button', { name: 'Save review' }));
  expect(await screen.findByText('Review saved.')).toBeInTheDocument();
  const review = planner().reviews.find((candidate) => candidate.weekStart === '2026-09-21');
  expect(review?.renewedDimensions).toEqual([]);
  expect(review?.accomplishments).toBe('');
});
