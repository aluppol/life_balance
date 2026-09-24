import { mondayOf, previousWeek } from '../../shared/calendar';
import { useToday } from '../../shared/useToday';
import { MondayRoute } from '../common/MondayRoute';
import { reviewPath } from '../common/paths';
import { WeekReview } from './WeekReview';

export function ReviewPage() {
  const lastMonday = previousWeek(mondayOf(useToday()));
  return (
    <MondayRoute fallbackMonday={lastMonday} pathOf={reviewPath}>
      {(monday) => <WeekReview monday={monday} lastMonday={lastMonday} />}
    </MondayRoute>
  );
}
