import type { CoreValue } from '../../api/coreValues';
import type { LifeRole } from '../../api/lifeRoles';
import { planner } from '../planner';
import { rankedHandlers } from './rankedHandlers';

export const valueHandlers = rankedHandlers<CoreValue>({
  path: '/api/values',
  noun: 'core value',
  entries: () => planner().values,
  replaceEntries: (values) => {
    planner().values = values;
  },
  newEntry: (id, details) => ({ id, ...details, position: 0 }),
  removalConflict: () => undefined,
  forget: (value) => {
    planner().goals = planner().goals.map((goal) => ({
      ...goal,
      valueIds: goal.valueIds.filter((valueId) => valueId !== value.id),
    }));
  },
});

export const roleHandlers = rankedHandlers<LifeRole>({
  path: '/api/roles',
  noun: 'life role',
  entries: () => planner().roles,
  replaceEntries: (roles) => {
    planner().roles = roles;
  },
  newEntry: (id, details) => ({ id, ...details, kind: 'PERSONAL', position: 0 }),
  removalConflict: roleRemovalConflict,
  forget: () => undefined,
});

function roleRemovalConflict(role: LifeRole): string | undefined {
  if (role.kind === 'SHARPEN_THE_SAW') {
    return `The ${role.name} role cannot be removed`;
  }
  const { goals, activities } = planner();
  const isInUse = [...goals, ...activities].some((record) => record.roleId === role.id);
  return isInUse ? `The ${role.name} role still has goals or activities` : undefined;
}
