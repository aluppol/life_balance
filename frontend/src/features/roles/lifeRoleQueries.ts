import { useQuery } from '@tanstack/react-query';
import { lifeRoleCollection } from '../../api/lifeRoles';
import { queryKeys } from '../common/queryKeys';

export function useLifeRoles() {
  return useQuery({ queryKey: queryKeys.lifeRoles, queryFn: lifeRoleCollection.fetchAll });
}
