import type { CoreValue } from '../../api/coreValues';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { type GoalFilter, isActive, matchesFilter } from './goalStatuses';

interface GoalsOfRole {
  readonly role: LifeRole;
  readonly goals: readonly Goal[];
}

export function goalsByRole(
  roles: readonly LifeRole[],
  goals: readonly Goal[],
  filter: GoalFilter,
): GoalsOfRole[] {
  return roles.map((role) => ({
    role,
    goals: goals.filter((goal) => goal.roleId === role.id && matchesFilter(goal, filter)),
  }));
}

export function valueNamesOf(goal: Goal, coreValues: readonly CoreValue[]): string[] {
  return coreValues.filter((value) => goal.valueIds.includes(value.id)).map((value) => value.name);
}

export function activeGoalCount(goals: readonly Goal[], roleId: string): number {
  return goals.filter((goal) => goal.roleId === roleId && isActive(goal)).length;
}
