import { fireEvent, render, screen } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import { SelectField } from './SelectField';

const quadrants = [
  { value: 'IMPORTANT_URGENT', label: 'Q1' },
  { value: 'IMPORTANT_NOT_URGENT', label: 'Q2' },
] as const;

test('passes on the chosen value', () => {
  const onChange = vi.fn();
  render(
    <SelectField
      label="Quadrant"
      value="IMPORTANT_URGENT"
      choices={quadrants}
      onChange={onChange}
    />,
  );
  fireEvent.change(screen.getByLabelText('Quadrant'), {
    target: { value: 'IMPORTANT_NOT_URGENT' },
  });
  expect(onChange).toHaveBeenCalledWith('IMPORTANT_NOT_URGENT');
  expect(screen.getByLabelText('Quadrant')).toHaveAttribute('aria-invalid', 'false');
});

test('ignores a value that is not one of the choices', () => {
  const onChange = vi.fn();
  render(
    <SelectField
      label="Quadrant"
      value="IMPORTANT_URGENT"
      choices={quadrants}
      onChange={onChange}
    />,
  );
  fireEvent.change(screen.getByLabelText('Quadrant'), { target: { value: 'SOMEDAY' } });
  expect(onChange).not.toHaveBeenCalled();
});

test('describes the field by its error', () => {
  render(
    <SelectField
      label="Quadrant"
      value="IMPORTANT_URGENT"
      choices={quadrants}
      error="must not be null"
      onChange={vi.fn()}
    />,
  );
  expect(screen.getByLabelText('Quadrant')).toHaveAccessibleDescription('must not be null');
  expect(screen.getByLabelText('Quadrant')).toHaveAttribute('aria-invalid', 'true');
});
