import { deleteAt, getJson, postJson, putEmpty, putJson } from './httpClient';
import type { Revision } from './revision';

export type Quadrant =
  'IMPORTANT_URGENT' | 'IMPORTANT_NOT_URGENT' | 'NOT_IMPORTANT_URGENT' | 'NOT_IMPORTANT_NOT_URGENT';

export interface ActivityDetails {
  readonly roleId: string;
  readonly goalId: string | null;
  readonly title: string;
  readonly quadrant: Quadrant;
  readonly scheduledOn: string | null;
}

export interface Activity extends ActivityDetails {
  readonly id: string;
  readonly weekStart: string;
  readonly isCompleted: boolean;
}

export function fetchWeekActivities(weekStart: string): Promise<Activity[]> {
  return getJson<Activity[]>(`/api/weeks/${weekStart}/activities`);
}

export function planActivity(weekStart: string, details: ActivityDetails): Promise<void> {
  return postJson(`/api/weeks/${weekStart}/activities`, details);
}

export function reviseActivity({ id, details }: Revision<ActivityDetails>): Promise<void> {
  return putJson(`/api/activities/${id}`, details);
}

export function completeActivity(id: string): Promise<void> {
  return putEmpty(`/api/activities/${id}/completion`);
}

export function reopenActivity(id: string): Promise<void> {
  return deleteAt(`/api/activities/${id}/completion`);
}

export function removeActivity(id: string): Promise<void> {
  return deleteAt(`/api/activities/${id}`);
}
