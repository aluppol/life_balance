import { deleteAt, getJson, postJson, putJson } from './httpClient';
import type { Revision } from './revision';

export type GoalStatus = 'ACTIVE' | 'ACHIEVED' | 'DROPPED';

export interface GoalDetails {
  readonly roleId: string;
  readonly title: string;
  readonly description: string;
  readonly dueOn: string | null;
  readonly valueIds: readonly string[];
}

export interface Goal extends GoalDetails {
  readonly id: string;
  readonly status: GoalStatus;
}

interface GoalStatusChange {
  readonly id: string;
  readonly status: GoalStatus;
}

const goalsPath = '/api/goals';

export function fetchGoals(): Promise<Goal[]> {
  return getJson<Goal[]>(goalsPath);
}

export function setGoal(details: GoalDetails): Promise<void> {
  return postJson(goalsPath, details);
}

export function reviseGoal({ id, details }: Revision<GoalDetails>): Promise<void> {
  return putJson(`${goalsPath}/${id}`, details);
}

export function changeGoalStatus({ id, status }: GoalStatusChange): Promise<void> {
  return putJson(`${goalsPath}/${id}/status`, { status });
}

export function removeGoal(id: string): Promise<void> {
  return deleteAt(`${goalsPath}/${id}`);
}
