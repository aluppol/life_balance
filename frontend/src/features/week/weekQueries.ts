import { useQuery } from '@tanstack/react-query';
import { fetchWeekActivities } from '../../api/activities';
import { fetchWeekScorecard } from '../../api/scorecard';
import { queryKeys } from '../common/queryKeys';

export function useWeekActivities(weekStart: string) {
  return useQuery({
    queryKey: queryKeys.weekActivities(weekStart),
    queryFn: () => fetchWeekActivities(weekStart),
  });
}

export function useWeekScorecard(weekStart: string) {
  return useQuery({
    queryKey: queryKeys.weekScorecard(weekStart),
    queryFn: () => fetchWeekScorecard(weekStart),
  });
}
