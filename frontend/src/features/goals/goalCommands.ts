import { useMutation } from '@tanstack/react-query';
import { changeGoalStatus, removeGoal, reviseGoal, setGoal } from '../../api/goals';
import { useInvalidation } from '../../shared/useInvalidation';
import { queryKeys } from '../common/queryKeys';

export function useSetGoal() {
  const invalidate = useInvalidation(queryKeys.goals);
  return useMutation({ mutationFn: setGoal, onSuccess: invalidate });
}

export function useReviseGoal() {
  const invalidate = useInvalidation(queryKeys.goals);
  return useMutation({ mutationFn: reviseGoal, onSuccess: invalidate });
}

export function useChangeGoalStatus() {
  const invalidate = useInvalidation(queryKeys.goals);
  return useMutation({ mutationFn: changeGoalStatus, onSuccess: invalidate });
}

export function useRemoveGoal() {
  const invalidate = useInvalidation(queryKeys.goals, queryKeys.allActivities);
  return useMutation({ mutationFn: removeGoal, onSuccess: invalidate });
}
