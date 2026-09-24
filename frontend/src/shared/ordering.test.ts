import { expect, test } from 'vitest';
import { moveEarlier, moveLater } from './ordering';

const ids = ['integrity', 'family', 'growth'];

test('moves an id one place earlier', () => {
  expect(moveEarlier(ids, 'family')).toEqual(['family', 'integrity', 'growth']);
  expect(moveEarlier(ids, 'growth')).toEqual(['integrity', 'growth', 'family']);
});

test('moves an id one place later', () => {
  expect(moveLater(ids, 'family')).toEqual(['integrity', 'growth', 'family']);
  expect(moveLater(ids, 'integrity')).toEqual(['family', 'integrity', 'growth']);
});

test('keeps the order when the id cannot move further', () => {
  expect(moveEarlier(ids, 'integrity')).toEqual(ids);
  expect(moveLater(ids, 'growth')).toEqual(ids);
});

test('keeps the order for an unknown id', () => {
  expect(moveEarlier(ids, 'courage')).toEqual(ids);
  expect(moveLater(ids, 'courage')).toEqual(ids);
});

test('leaves the given order untouched', () => {
  moveLater(ids, 'integrity');
  moveEarlier(ids, 'growth');
  expect(ids).toEqual(['integrity', 'family', 'growth']);
});
