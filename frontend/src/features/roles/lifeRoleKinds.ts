import type { LifeRole } from '../../api/lifeRoles';

export function isRemovable(role: LifeRole): boolean {
  return !isBuiltIn(role);
}

export function builtInBadge(role: LifeRole): string | undefined {
  return isBuiltIn(role) ? 'Built in' : undefined;
}

function isBuiltIn(role: LifeRole): boolean {
  return role.kind === 'SHARPEN_THE_SAW';
}
