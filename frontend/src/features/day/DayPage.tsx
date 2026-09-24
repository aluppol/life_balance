import { useParams } from 'react-router';
import { useToday } from '../../shared/useToday';
import { resolveDay } from '../common/dateParams';
import { NotFoundPage } from '../notFound/NotFoundPage';
import { DayAgenda } from './DayAgenda';

export function DayPage() {
  const { date } = useParams();
  const today = useToday();
  const request = resolveDay(date, today);
  if (request.kind === 'invalid') {
    return <NotFoundPage />;
  }
  return <DayAgenda day={request.day} today={today} />;
}
