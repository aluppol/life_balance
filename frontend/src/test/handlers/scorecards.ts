import type { Activity, Quadrant } from '../../api/activities';
import type { Tally, WeekScorecard } from '../../api/scorecard';
import { quadrantOrder } from '../../features/week/quadrants';

export function scorecardOf(monday: string, activities: readonly Activity[]): WeekScorecard {
  const roleIds = [...new Set(activities.map((activity) => activity.roleId))].sort();
  return {
    weekStart: monday,
    overall: tallyOf(activities),
    bigRocks: tallyOf(inQuadrant(activities, 'IMPORTANT_NOT_URGENT')),
    byQuadrant: Object.fromEntries(
      quadrantOrder.map((quadrant) => [quadrant, tallyOf(inQuadrant(activities, quadrant))]),
    ) as Record<Quadrant, Tally>,
    byRole: roleIds.map((roleId) => ({
      roleId,
      ...tallyOf(activities.filter((activity) => activity.roleId === roleId)),
    })),
  };
}

function inQuadrant(activities: readonly Activity[], quadrant: Quadrant): Activity[] {
  return activities.filter((activity) => activity.quadrant === quadrant);
}

function tallyOf(activities: readonly Activity[]): Tally {
  return {
    planned: activities.length,
    completed: activities.filter((activity) => activity.isCompleted).length,
  };
}
