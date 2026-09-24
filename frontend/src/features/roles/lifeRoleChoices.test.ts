import { expect, test } from 'vitest';
import { seededRoles } from '../../test/seed';
import { firstRoleId, roleChoices, roleNameOf } from './lifeRoleChoices';

test('offers every role as a choice in role order', () => {
  expect(roleChoices(seededRoles).map((choice) => choice.label)).toEqual([
    'Parent',
    'Engineer',
    'Sharpen the Saw',
    'Friend',
  ]);
  expect(roleChoices(seededRoles)[0]?.value).toBe('role-parent');
});

test('answers the first role, or no role for a person without roles', () => {
  expect(firstRoleId(seededRoles)).toBe('role-parent');
  expect(firstRoleId([])).toBe('');
});

test('names a role and admits an unknown one', () => {
  expect(roleNameOf(seededRoles, 'role-saw')).toBe('Sharpen the Saw');
  expect(roleNameOf(seededRoles, 'role-gone')).toBe('Unknown role');
});
