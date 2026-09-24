import { useMutation } from '@tanstack/react-query';
import { defineMissionStatement } from '../../api/missionStatement';
import { useInvalidation } from '../../shared/useInvalidation';
import { queryKeys } from '../common/queryKeys';

export function useDefineMissionStatement() {
  const invalidate = useInvalidation(queryKeys.missionStatement);
  return useMutation({ mutationFn: defineMissionStatement, onSuccess: invalidate });
}
