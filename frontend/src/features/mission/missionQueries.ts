import { useQuery } from '@tanstack/react-query';
import { fetchMissionStatement } from '../../api/missionStatement';
import { queryKeys } from '../common/queryKeys';

export function useMissionStatement() {
  return useQuery({ queryKey: queryKeys.missionStatement, queryFn: fetchMissionStatement });
}
