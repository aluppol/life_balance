import type { RouteObject } from 'react-router';
import { CompassPage } from '../features/compass/CompassPage';
import { DayPage } from '../features/day/DayPage';
import { GoalsPage } from '../features/goals/GoalsPage';
import { MissionPage } from '../features/mission/MissionPage';
import { NotFoundPage } from '../features/notFound/NotFoundPage';
import { ReviewPage } from '../features/review/ReviewPage';
import { RolesPage } from '../features/roles/RolesPage';
import { ValuesPage } from '../features/values/ValuesPage';
import { WeekPage } from '../features/week/WeekPage';
import { AppShell } from './AppShell';

export const routes: RouteObject[] = [
  {
    element: <AppShell />,
    children: [
      { index: true, element: <CompassPage /> },
      { path: 'mission', element: <MissionPage /> },
      { path: 'values', element: <ValuesPage /> },
      { path: 'roles', element: <RolesPage /> },
      { path: 'goals', element: <GoalsPage /> },
      { path: 'week/:monday?', element: <WeekPage /> },
      { path: 'day/:date?', element: <DayPage /> },
      { path: 'review/:monday?', element: <ReviewPage /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
];
