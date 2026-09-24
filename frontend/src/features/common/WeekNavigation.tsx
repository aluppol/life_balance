import { nextWeek, previousWeek } from '../../shared/calendar';
import { PeriodNavigation } from '../../shared/ui/PeriodNavigation';

interface WeekNavigationProps {
  readonly monday: string;
  readonly anchorMonday: string;
  readonly anchorLabel: string;
  readonly pathOf: (monday: string) => string;
}

export function WeekNavigation({ monday, anchorMonday, anchorLabel, pathOf }: WeekNavigationProps) {
  return (
    <PeriodNavigation
      label="Weeks"
      previous={{ to: pathOf(previousWeek(monday)), label: 'Previous week' }}
      anchor={{ to: pathOf(anchorMonday), label: anchorLabel, isShown: monday === anchorMonday }}
      next={{ to: pathOf(nextWeek(monday)), label: 'Next week' }}
    />
  );
}
