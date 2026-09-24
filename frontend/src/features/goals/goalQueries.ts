import { useQuery } from '@tanstack/react-query';
import { fetchGoals } from '../../api/goals';
import { queryKeys } from '../common/queryKeys';

export function useGoals() {
  return useQuery({ queryKey: queryKeys.goals, queryFn: fetchGoals });
}
