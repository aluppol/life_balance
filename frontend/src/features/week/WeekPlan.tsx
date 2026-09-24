import { formatCalendarDate } from '../../shared/calendar';
import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { weekPath } from '../common/paths';
import { WeekNavigation } from '../common/WeekNavigation';
import { useGoals } from '../goals/goalQueries';
import { useLifeRoles } from '../roles/lifeRoleQueries';
import { WeekBoard } from './WeekBoard';
import { useWeekActivities } from './weekQueries';

interface WeekPlanProps {
  readonly monday: string;
  readonly currentMonday: string;
}

export function WeekPlan({ monday, currentMonday }: WeekPlanProps) {
  const activities = useWeekActivities(monday);
  const roles = useLifeRoles();
  const goals = useGoals();
  return (
    <Page
      title={`Week of ${formatCalendarDate(monday)}`}
      intro="Habit 3: Put first things first. Schedule your big rocks, the important but not urgent activities of each role, before the week fills up with everything else."
    >
      <WeekNavigation
        monday={monday}
        anchorMonday={currentMonday}
        anchorLabel="This week"
        pathOf={weekPath}
      />
      <Loadable queries={[activities, roles, goals]}>
        {(weekActivities, lifeRoles, allGoals) => (
          <WeekBoard
            key={monday}
            monday={monday}
            activities={weekActivities}
            roles={lifeRoles}
            goals={allGoals}
          />
        )}
      </Loadable>
    </Page>
  );
}
