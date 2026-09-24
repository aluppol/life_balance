import { expect, test } from 'vitest';
import { describedBy, errorIdOf, hintIdOf } from './fieldDescriptions';

test('names the hint and the error of a field', () => {
  expect(hintIdOf('mission')).toBe('mission-hint');
  expect(errorIdOf('mission')).toBe('mission-error');
});

test('describes a field by its hint and its error when they exist', () => {
  expect(describedBy('mission', '12 of 4000 characters', 'must not be blank')).toBe(
    'mission-hint mission-error',
  );
  expect(describedBy('mission', '12 of 4000 characters', undefined)).toBe('mission-hint');
  expect(describedBy('mission', undefined, 'must not be blank')).toBe('mission-error');
  expect(describedBy('mission', undefined, undefined)).toBeUndefined();
});
