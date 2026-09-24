import { useMutation } from '@tanstack/react-query';
import {
  type ActivityDetails,
  completeActivity,
  planActivity,
  removeActivity,
  reopenActivity,
  reviseActivity,
} from '../../api/activities';
import { useInvalidation } from '../../shared/useInvalidation';
import { queryKeys } from '../common/queryKeys';

export function usePlanActivity(weekStart: string) {
  const invalidate = useWeekInvalidation(weekStart);
  return useMutation({
    mutationFn: (details: ActivityDetails) => planActivity(weekStart, details),
    onSuccess: invalidate,
  });
}

export function useReviseActivity(weekStart: string) {
  const invalidate = useWeekInvalidation(weekStart);
  return useMutation({ mutationFn: reviseActivity, onSuccess: invalidate });
}

export function useCompleteActivity(weekStart: string) {
  const invalidate = useWeekInvalidation(weekStart);
  return useMutation({ mutationFn: completeActivity, onSuccess: invalidate });
}

export function useReopenActivity(weekStart: string) {
  const invalidate = useWeekInvalidation(weekStart);
  return useMutation({ mutationFn: reopenActivity, onSuccess: invalidate });
}

export function useRemoveActivity(weekStart: string) {
  const invalidate = useWeekInvalidation(weekStart);
  return useMutation({ mutationFn: removeActivity, onSuccess: invalidate });
}

function useWeekInvalidation(weekStart: string) {
  return useInvalidation(queryKeys.weekActivities(weekStart), queryKeys.weekScorecard(weekStart));
}
