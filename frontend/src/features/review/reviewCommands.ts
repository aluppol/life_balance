import { useMutation } from '@tanstack/react-query';
import { recordWeeklyReview, type WeeklyReviewDetails } from '../../api/weeklyReview';
import { useInvalidation } from '../../shared/useInvalidation';
import { queryKeys } from '../common/queryKeys';

export function useRecordWeeklyReview(weekStart: string) {
  const invalidate = useInvalidation(queryKeys.weeklyReview(weekStart));
  return useMutation({
    mutationFn: (details: WeeklyReviewDetails) => recordWeeklyReview(weekStart, details),
    onSuccess: invalidate,
  });
}
