import { fireEvent, render, screen } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import { Form } from './Form';

test('submits without leaving the page', () => {
  const onSubmit = vi.fn();
  render(
    <Form label="Mission statement" onSubmit={onSubmit}>
      <button type="submit">Save mission</button>
    </Form>,
  );
  const isDefaultAllowed = fireEvent.submit(
    screen.getByRole('form', { name: 'Mission statement' }),
  );
  expect(isDefaultAllowed).toBe(false);
  expect(onSubmit).toHaveBeenCalledTimes(1);
});
