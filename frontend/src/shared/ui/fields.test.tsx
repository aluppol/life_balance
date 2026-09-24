import { render, screen } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import { DateField } from './DateField';
import { RequiredTextField } from './RequiredTextField';
import { TextAreaField } from './TextAreaField';

test('shows a valid field without an error or a hint', () => {
  render(
    <>
      <RequiredTextField label="Name" value="Courage" maxLength={100} onChange={vi.fn()} />
      <TextAreaField label="Description" value="" maxLength={1000} rows={3} onChange={vi.fn()} />
      <DateField label="Due date" value="" onChange={vi.fn()} />
    </>,
  );
  for (const label of ['Name', 'Description', 'Due date']) {
    expect(screen.getByLabelText(label)).toHaveAttribute('aria-invalid', 'false');
    expect(screen.getByLabelText(label)).not.toHaveAttribute('aria-describedby');
  }
  expect(screen.queryAllByRole('paragraph')).toHaveLength(0);
});

test('describes a field by its hint and its error', () => {
  render(
    <TextAreaField
      label="Lessons"
      value=""
      maxLength={4000}
      rows={3}
      hint="0 of 4000 characters"
      error="size must be between 0 and 4000"
      onChange={vi.fn()}
    />,
  );
  const lessons = screen.getByLabelText('Lessons');
  expect(lessons).toHaveAttribute('aria-invalid', 'true');
  expect(lessons).toHaveAccessibleDescription(
    '0 of 4000 characters size must be between 0 and 4000',
  );
});

test('marks an invalid date and a required text with their errors', () => {
  render(
    <>
      <DateField label="Due date" value="" error="must be a future date" onChange={vi.fn()} />
      <RequiredTextField
        label="Title"
        value=""
        maxLength={200}
        error="must not be blank"
        onChange={vi.fn()}
      />
    </>,
  );
  expect(screen.getByLabelText('Due date')).toHaveAttribute('aria-invalid', 'true');
  expect(screen.getByLabelText('Due date')).toHaveAccessibleDescription('must be a future date');
  expect(screen.getByLabelText('Title')).toHaveAttribute('aria-invalid', 'true');
  expect(screen.getByLabelText('Title')).toBeRequired();
});
