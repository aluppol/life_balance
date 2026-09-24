import type { Quadrant } from './activities';
import { getJson } from './httpClient';

export interface Tally {
  readonly planned: number;
  readonly completed: number;
}

export interface RoleTally extends Tally {
  readonly roleId: string;
}

export interface WeekScorecard {
  readonly weekStart: string;
  readonly overall: Tally;
  readonly bigRocks: Tally;
  readonly byQuadrant: Readonly<Record<Quadrant, Tally>>;
  readonly byRole: readonly RoleTally[];
}

export function fetchWeekScorecard(weekStart: string): Promise<WeekScorecard> {
  return getJson<WeekScorecard>(`/api/weeks/${weekStart}/scorecard`);
}
