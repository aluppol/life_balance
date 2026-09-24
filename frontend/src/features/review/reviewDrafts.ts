import type { WeeklyReview, WeeklyReviewDetails } from '../../api/weeklyReview';

export function detailsOfReview(review: WeeklyReview | null): WeeklyReviewDetails {
  return {
    accomplishments: review?.accomplishments ?? '',
    lessons: review?.lessons ?? '',
    renewedDimensions: review?.renewedDimensions ?? [],
  };
}
