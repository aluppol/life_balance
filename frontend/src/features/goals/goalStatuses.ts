import type { Goal, GoalStatus } from '../../api/goals';
import type { Choice } from '../../shared/ui/choice';

export type GoalFilter = GoalStatus | 'ALL';

export const goalFilterChoices: readonly Choice<GoalFilter>[] = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'ACHIEVED', label: 'Achieved' },
  { value: 'DROPPED', label: 'Dropped' },
  { value: 'ALL', label: 'All' },
];

export const goalStatusLabels: Readonly<Record<GoalStatus, string>> = {
  ACTIVE: 'Active',
  ACHIEVED: 'Achieved',
  DROPPED: 'Dropped',
};

export function matchesFilter(goal: Goal, filter: GoalFilter): boolean {
  return filter === 'ALL' || goal.status === filter;
}

export function isActive(goal: Goal): boolean {
  return goal.status === 'ACTIVE';
}
