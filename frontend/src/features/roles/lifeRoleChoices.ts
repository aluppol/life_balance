import type { LifeRole } from '../../api/lifeRoles';
import type { Choice } from '../../shared/ui/choice';

export function roleChoices(roles: readonly LifeRole[]): Choice<string>[] {
  return roles.map((role) => ({ value: role.id, label: role.name }));
}

export function firstRoleId(roles: readonly LifeRole[]): string {
  return roles[0]?.id ?? '';
}

export function roleNameOf(roles: readonly LifeRole[], roleId: string): string {
  return roles.find((role) => role.id === roleId)?.name ?? 'Unknown role';
}
