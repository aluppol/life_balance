import type { Activity, Quadrant } from '../../api/activities';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { formatWeekday } from '../../shared/calendar';
import { roleNameOf } from '../roles/lifeRoleChoices';
import { quadrantLabels } from './quadrants';

export function activitiesInQuadrant(
  activities: readonly Activity[],
  quadrant: Quadrant,
): Activity[] {
  return activities.filter((activity) => activity.quadrant === quadrant);
}

export function activitiesOn(activities: readonly Activity[], day: string): Activity[] {
  return activities.filter((activity) => activity.scheduledOn === day);
}

export function unscheduledActivities(activities: readonly Activity[]): Activity[] {
  return activities.filter((activity) => activity.scheduledOn === null);
}

export function purposeOf(
  activity: Activity,
  roles: readonly LifeRole[],
  goals: readonly Goal[],
): string {
  const goal = goals.find((candidate) => candidate.id === activity.goalId);
  const roleName = roleNameOf(roles, activity.roleId);
  return goal === undefined ? roleName : `${roleName} · ${goal.title}`;
}

export function scheduleOf(activity: Activity): string {
  return activity.scheduledOn === null ? 'No day' : formatWeekday(activity.scheduledOn);
}

export function quadrantOf(activity: Activity): string {
  return quadrantLabels[activity.quadrant];
}
