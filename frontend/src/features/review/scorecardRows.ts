import type { LifeRole } from '../../api/lifeRoles';
import type { RoleTally, WeekScorecard } from '../../api/scorecard';
import { roleNameOf } from '../roles/lifeRoleChoices';
import { quadrantLabels, quadrantOrder } from '../week/quadrants';

export interface TallyRow {
  readonly key: string;
  readonly label: string;
  readonly planned: number;
  readonly completed: number;
}

export function quadrantRows(scorecard: WeekScorecard): TallyRow[] {
  return quadrantOrder.map((quadrant) => ({
    key: quadrant,
    label: quadrantLabels[quadrant],
    planned: scorecard.byQuadrant[quadrant].planned,
    completed: scorecard.byQuadrant[quadrant].completed,
  }));
}

export function roleRows(scorecard: WeekScorecard, roles: readonly LifeRole[]): TallyRow[] {
  const rankOf = (tally: RoleTally) => rankInRoles(roles, tally.roleId);
  return [...scorecard.byRole]
    .sort((first, second) => rankOf(first) - rankOf(second))
    .map((tally) => ({
      key: tally.roleId,
      label: roleNameOf(roles, tally.roleId),
      planned: tally.planned,
      completed: tally.completed,
    }));
}

function rankInRoles(roles: readonly LifeRole[], roleId: string): number {
  const index = roles.findIndex((role) => role.id === roleId);
  return index === -1 ? roles.length : index;
}
