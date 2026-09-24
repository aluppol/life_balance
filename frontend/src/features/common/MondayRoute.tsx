import type { ReactNode } from 'react';
import { Navigate, useParams } from 'react-router';
import { NotFoundPage } from '../notFound/NotFoundPage';
import { resolveMonday } from './dateParams';

interface MondayRouteProps {
  readonly fallbackMonday: string;
  readonly pathOf: (monday: string) => string;
  readonly children: (monday: string) => ReactNode;
}

export function MondayRoute({ fallbackMonday, pathOf, children }: MondayRouteProps) {
  const { monday } = useParams();
  const request = resolveMonday(monday, fallbackMonday);
  if (request.kind === 'invalid') {
    return <NotFoundPage />;
  }
  if (request.kind === 'redirect') {
    return <Navigate replace to={pathOf(request.monday)} />;
  }
  return children(request.monday);
}
