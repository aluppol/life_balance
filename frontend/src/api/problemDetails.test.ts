import { expect, test } from 'vitest';
import { parseProblemDetails } from './problemDetails';

test('reads the detail and the text field errors of a problem', () => {
  const body = JSON.stringify({
    title: 'Bad Request',
    detail: 'Some fields are invalid',
    errors: { name: 'must not be blank', position: 3 },
  });
  expect(parseProblemDetails(400, body)).toEqual({
    status: 400,
    detail: 'Some fields are invalid',
    fieldErrors: { name: 'must not be blank' },
  });
});

test('falls back to the title when the detail is missing or blank', () => {
  expect(parseProblemDetails(409, JSON.stringify({ title: 'Conflict' })).detail).toBe('Conflict');
  expect(parseProblemDetails(409, JSON.stringify({ title: 'Conflict', detail: '  ' })).detail).toBe(
    'Conflict',
  );
});

test('describes a server failure without a problem body', () => {
  expect(parseProblemDetails(500, '')).toEqual({
    status: 500,
    detail: 'Life Balance could not complete the request. Please try again.',
    fieldErrors: {},
  });
});

test('describes a client failure without a readable problem by its status', () => {
  expect(parseProblemDetails(499, 'teapot').detail).toBe('The request failed with status 499.');
  expect(parseProblemDetails(400, 'null').detail).toBe('The request failed with status 400.');
  expect(parseProblemDetails(400, '["not", "a", "problem"]').detail).toBe(
    'The request failed with status 400.',
  );
});

test('ignores field errors that are not an object', () => {
  const problemWith = (errors: unknown) => JSON.stringify({ detail: 'Bad', errors });
  expect(parseProblemDetails(400, problemWith('name')).fieldErrors).toEqual({});
  expect(parseProblemDetails(400, problemWith(['name'])).fieldErrors).toEqual({});
  expect(parseProblemDetails(400, problemWith(null)).fieldErrors).toEqual({});
});
