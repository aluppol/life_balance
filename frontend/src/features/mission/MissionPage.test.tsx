import { screen } from '@testing-library/react';
import { expect, test } from 'vitest';
import { planner } from '../../test/planner';
import { renderRoute } from '../../test/renderRoute';

test('shows the mission with its character count and the Habit 2 hint', async () => {
  renderRoute('/mission');
  const mission = await screen.findByLabelText('Your mission statement');
  expect(mission).toHaveValue('I live by principles I choose, not by moods I happen to have.');
  expect(mission).toHaveAccessibleDescription('61 of 4000 characters');
  expect(mission).toHaveAttribute('maxlength', '4000');
  expect(mission).toHaveAttribute('aria-invalid', 'false');
  expect(screen.getByText(/Habit 2: Begin with the end in mind/)).toBeInTheDocument();
  expect(screen.queryByText(/You have not written your mission yet/)).not.toBeInTheDocument();
});

test('counts the characters while typing and saves the mission', async () => {
  const { user } = renderRoute('/mission');
  const mission = await screen.findByLabelText('Your mission statement');
  await user.clear(mission);
  await user.type(mission, 'Be kind.');
  expect(mission).toHaveAccessibleDescription('8 of 4000 characters');
  await user.click(screen.getByRole('button', { name: 'Save mission' }));
  expect(await screen.findByText('Mission saved.')).toBeInTheDocument();
  expect(planner().mission).toEqual({ text: 'Be kind.' });
});

test('shows the field error for a blank mission', async () => {
  const { user } = renderRoute('/mission');
  const mission = await screen.findByLabelText('Your mission statement');
  await user.clear(mission);
  await user.type(mission, '  ');
  await user.click(screen.getByRole('button', { name: 'Save mission' }));
  expect(await screen.findByText('must not be blank')).toBeInTheDocument();
  expect(mission).toHaveAttribute('aria-invalid', 'true');
  expect(mission).toHaveAccessibleDescription('2 of 4000 characters must not be blank');
  expect(screen.queryByText('Mission saved.')).not.toBeInTheDocument();
});

test('invites writing a mission when there is none yet', async () => {
  planner().mission = null;
  const { user } = renderRoute('/mission');
  expect(await screen.findByText(/You have not written your mission yet/)).toBeInTheDocument();
  const mission = screen.getByLabelText('Your mission statement');
  expect(mission).toHaveValue('');
  await user.type(mission, 'Begin with the end in mind.');
  await user.click(screen.getByRole('button', { name: 'Save mission' }));
  expect(await screen.findByText('Mission saved.')).toBeInTheDocument();
  expect(planner().mission).toEqual({ text: 'Begin with the end in mind.' });
});
