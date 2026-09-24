import type { Activity, ActivityDetails, Quadrant } from '../../api/activities';
import type { Goal } from '../../api/goals';
import { formatWeekdayAndDate, weekDays } from '../../shared/calendar';
import { nullWhenEmpty } from '../../shared/text';
import type { Choice } from '../../shared/ui/choice';
import { isActive } from '../goals/goalStatuses';
import { bigRockQuadrant } from './quadrants';

export interface ActivityDraft {
  readonly title: string;
  readonly roleId: string;
  readonly goalId: string;
  readonly quadrant: Quadrant;
  readonly scheduledOn: string;
}

export function emptyActivityDraft(roleId: string): ActivityDraft {
  return { title: '', roleId, goalId: '', quadrant: bigRockQuadrant, scheduledOn: '' };
}

export function draftOfActivity(activity: Activity): ActivityDraft {
  return {
    title: activity.title,
    roleId: activity.roleId,
    goalId: activity.goalId ?? '',
    quadrant: activity.quadrant,
    scheduledOn: activity.scheduledOn ?? '',
  };
}

export function detailsOfActivityDraft(draft: ActivityDraft): ActivityDetails {
  return {
    ...draft,
    goalId: nullWhenEmpty(draft.goalId),
    scheduledOn: nullWhenEmpty(draft.scheduledOn),
  };
}

export function withConsistentGoal(draft: ActivityDraft, goals: readonly Goal[]): ActivityDraft {
  const goal = goals.find((candidate) => candidate.id === draft.goalId);
  return goal === undefined || goal.roleId === draft.roleId ? draft : { ...draft, goalId: '' };
}

export function goalChoicesFor(
  goals: readonly Goal[],
  roleId: string,
  keptGoalId: string | null,
): Choice<string>[] {
  const choosable = goals.filter(
    (goal) => goal.roleId === roleId && (isActive(goal) || goal.id === keptGoalId),
  );
  return [
    { value: '', label: 'No goal' },
    ...choosable.map((goal) => ({ value: goal.id, label: goal.title })),
  ];
}

export function dayChoicesOf(monday: string): Choice<string>[] {
  return [
    { value: '', label: 'No day' },
    ...weekDays(monday).map((day) => ({ value: day, label: formatWeekdayAndDate(day) })),
  ];
}
