import { Link } from 'react-router';
import { addDays, formatCalendarDate, formatLongDate, mondayOf } from '../../shared/calendar';
import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { PeriodNavigation } from '../../shared/ui/PeriodNavigation';
import { dayPath, weekPath } from '../common/paths';
import { useGoals } from '../goals/goalQueries';
import { useLifeRoles } from '../roles/lifeRoleQueries';
import { activitiesOn } from '../week/activityFacts';
import { useWeekActivities } from '../week/weekQueries';
import { DayActivities } from './DayActivities';

interface DayAgendaProps {
  readonly day: string;
  readonly today: string;
}

export function DayAgenda({ day, today }: DayAgendaProps) {
  const monday = mondayOf(day);
  const activities = useWeekActivities(monday);
  const roles = useLifeRoles();
  const goals = useGoals();
  return (
    <Page
      title={formatLongDate(day)}
      intro="One day at a time: tick off what you have done. Plan and move activities in the week."
    >
      <DayNavigation day={day} today={today} />
      <Loadable queries={[activities, roles, goals]}>
        {(weekActivities, lifeRoles, allGoals) => (
          <DayActivities
            monday={monday}
            activities={activitiesOn(weekActivities, day)}
            roles={lifeRoles}
            goals={allGoals}
          />
        )}
      </Loadable>
      <Link to={weekPath(monday)}>{`Open the week of ${formatCalendarDate(monday)}`}</Link>
    </Page>
  );
}

function DayNavigation({ day, today }: DayAgendaProps) {
  return (
    <PeriodNavigation
      label="Days"
      previous={{ to: dayPath(addDays(day, -1)), label: 'Previous day' }}
      anchor={{ to: dayPath(today), label: 'Today', isShown: day === today }}
      next={{ to: dayPath(addDays(day, 1)), label: 'Next day' }}
    />
  );
}
