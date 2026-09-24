import { getOptionalJson, putJson } from './httpClient';

export type RenewalDimension = 'PHYSICAL' | 'MENTAL' | 'SOCIAL_EMOTIONAL' | 'SPIRITUAL';

export interface WeeklyReviewDetails {
  readonly accomplishments: string;
  readonly lessons: string;
  readonly renewedDimensions: readonly RenewalDimension[];
}

export interface WeeklyReview extends WeeklyReviewDetails {
  readonly weekStart: string;
}

export function fetchWeeklyReview(weekStart: string): Promise<WeeklyReview | null> {
  return getOptionalJson<WeeklyReview>(`/api/weeks/${weekStart}/review`);
}

export function recordWeeklyReview(weekStart: string, details: WeeklyReviewDetails): Promise<void> {
  return putJson(`/api/weeks/${weekStart}/review`, details);
}
