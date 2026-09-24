import { useQuery } from '@tanstack/react-query';
import { coreValueCollection } from '../../api/coreValues';
import { queryKeys } from '../common/queryKeys';

export function useCoreValues() {
  return useQuery({ queryKey: queryKeys.coreValues, queryFn: coreValueCollection.fetchAll });
}
