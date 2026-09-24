import { useMutation } from '@tanstack/react-query';
import { lifeRoleCollection } from '../../api/lifeRoles';
import { useInvalidation } from '../../shared/useInvalidation';
import { queryKeys } from '../common/queryKeys';
import type { RankedEntryCommands } from '../ranking/rankedEntry';

export function useAddLifeRole() {
  const invalidate = useInvalidation(queryKeys.lifeRoles);
  return useMutation({ mutationFn: lifeRoleCollection.add, onSuccess: invalidate });
}

export function useLifeRoleCommands(): RankedEntryCommands {
  return {
    revision: useReviseLifeRole(),
    removal: useRemoveLifeRole(),
    reorder: useReorderLifeRoles(),
  };
}

function useReviseLifeRole() {
  const invalidate = useInvalidation(queryKeys.lifeRoles);
  return useMutation({ mutationFn: lifeRoleCollection.revise, onSuccess: invalidate });
}

function useRemoveLifeRole() {
  const invalidate = useInvalidation(queryKeys.lifeRoles);
  return useMutation({ mutationFn: lifeRoleCollection.remove, onSuccess: invalidate });
}

function useReorderLifeRoles() {
  const invalidate = useInvalidation(queryKeys.lifeRoles);
  return useMutation({ mutationFn: lifeRoleCollection.reorder, onSuccess: invalidate });
}
