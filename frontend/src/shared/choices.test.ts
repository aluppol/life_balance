import { expect, test } from 'vitest';
import { excluding, including } from './choices';

const allInOrder = ['PHYSICAL', 'MENTAL', 'SOCIAL_EMOTIONAL', 'SPIRITUAL'];

test('includes a choice in the order of all choices', () => {
  expect(including(['SPIRITUAL'], 'MENTAL', allInOrder)).toEqual(['MENTAL', 'SPIRITUAL']);
});

test('includes an already selected choice only once', () => {
  expect(including(['MENTAL'], 'MENTAL', allInOrder)).toEqual(['MENTAL']);
});

test('excludes a choice and keeps the others', () => {
  expect(excluding(['PHYSICAL', 'MENTAL'], 'PHYSICAL')).toEqual(['MENTAL']);
  expect(excluding(['PHYSICAL'], 'SPIRITUAL')).toEqual(['PHYSICAL']);
});
