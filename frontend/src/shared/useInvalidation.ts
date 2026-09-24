import { type QueryKey, useQueryClient } from '@tanstack/react-query';

export function useInvalidation(...queryKeys: readonly QueryKey[]): () => Promise<void> {
  const queryClient = useQueryClient();
  return async () => {
    await Promise.all(queryKeys.map((queryKey) => queryClient.invalidateQueries({ queryKey })));
  };
}
