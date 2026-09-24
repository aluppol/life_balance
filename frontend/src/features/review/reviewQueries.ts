import { useQuery } from '@tanstack/react-query';
import { fetchWeeklyReview } from '../../api/weeklyReview';
import { queryKeys } from '../common/queryKeys';

export function useWeeklyReview(weekStart: string) {
  return useQuery({
    queryKey: queryKeys.weeklyReview(weekStart),
    queryFn: () => fetchWeeklyReview(weekStart),
  });
}
