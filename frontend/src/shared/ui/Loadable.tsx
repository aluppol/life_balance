import type { UseQueryResult } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { LoadFailure, LoadingMessage } from './Messages';

type LoadedContents<Queries extends readonly UseQueryResult[]> = {
  [Index in keyof Queries]: Queries[Index] extends UseQueryResult<infer Content> ? Content : never;
};

interface LoadableProps<Queries extends readonly UseQueryResult[]> {
  readonly queries: Queries;
  readonly children: (...contents: LoadedContents<Queries>) => ReactNode;
}

export function Loadable<const Queries extends readonly UseQueryResult[]>({
  queries,
  children,
}: LoadableProps<Queries>) {
  const failed = queries.find((query) => query.isLoadingError);
  if (failed !== undefined) {
    return <LoadFailure message={failed.error.message} onRetry={() => void failed.refetch()} />;
  }
  if (queries.some((query) => query.isPending)) {
    return <LoadingMessage />;
  }
  return children(...(queries.map((query) => query.data) as LoadedContents<Queries>));
}
