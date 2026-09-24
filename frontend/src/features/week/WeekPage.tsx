import { mondayOf } from '../../shared/calendar';
import { useToday } from '../../shared/useToday';
import { MondayRoute } from '../common/MondayRoute';
import { weekPath } from '../common/paths';
import { WeekPlan } from './WeekPlan';

export function WeekPage() {
  const currentMonday = mondayOf(useToday());
  return (
    <MondayRoute fallbackMonday={currentMonday} pathOf={weekPath}>
      {(monday) => <WeekPlan monday={monday} currentMonday={currentMonday} />}
    </MondayRoute>
  );
}
