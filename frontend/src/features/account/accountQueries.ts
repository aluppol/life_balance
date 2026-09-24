import { useQuery } from '@tanstack/react-query';
import { fetchSignedInPerson } from '../../api/signedInPerson';
import { queryKeys } from '../common/queryKeys';

export function useSignedInPerson() {
  return useQuery({ queryKey: queryKeys.signedInPerson, queryFn: fetchSignedInPerson });
}
