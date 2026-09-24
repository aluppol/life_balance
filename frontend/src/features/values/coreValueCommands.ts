import { useMutation } from '@tanstack/react-query';
import { coreValueCollection } from '../../api/coreValues';
import { useInvalidation } from '../../shared/useInvalidation';
import { queryKeys } from '../common/queryKeys';
import type { RankedEntryCommands } from '../ranking/rankedEntry';

export function useAddCoreValue() {
  const invalidate = useInvalidation(queryKeys.coreValues);
  return useMutation({ mutationFn: coreValueCollection.add, onSuccess: invalidate });
}

export function useCoreValueCommands(): RankedEntryCommands {
  return {
    revision: useReviseCoreValue(),
    removal: useRemoveCoreValue(),
    reorder: useReorderCoreValues(),
  };
}

function useReviseCoreValue() {
  const invalidate = useInvalidation(queryKeys.coreValues);
  return useMutation({ mutationFn: coreValueCollection.revise, onSuccess: invalidate });
}

function useRemoveCoreValue() {
  const invalidate = useInvalidation(queryKeys.coreValues, queryKeys.goals);
  return useMutation({ mutationFn: coreValueCollection.remove, onSuccess: invalidate });
}

function useReorderCoreValues() {
  const invalidate = useInvalidation(queryKeys.coreValues);
  return useMutation({ mutationFn: coreValueCollection.reorder, onSuccess: invalidate });
}
