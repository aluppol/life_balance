import { expect, test } from 'vitest';
import { nullWhenEmpty } from './text';

test('turns an empty text into nothing and keeps any other text', () => {
  expect(nullWhenEmpty('')).toBeNull();
  expect(nullWhenEmpty('2026-09-23')).toBe('2026-09-23');
  expect(nullWhenEmpty(' ')).toBe(' ');
});
