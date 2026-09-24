import type { Activity } from '../../api/activities';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { FailureMessage } from '../../shared/ui/Messages';
import { ActivityCheckbox } from '../week/ActivityCheckbox';
import { useActivityActions } from '../week/useActivityActions';
import styles from '../week/Week.module.css';
import { purposeOf, quadrantOf } from '../week/activityFacts';

interface DayActivitiesProps {
  readonly monday: string;
  readonly activities: readonly Activity[];
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
}

export function DayActivities({ monday, activities, roles, goals }: DayActivitiesProps) {
  const actions = useActivityActions(monday);
  if (activities.length === 0) {
    return <p className={styles.empty}>Nothing is scheduled for this day.</p>;
  }
  return (
    <>
      <FailureMessage message={actions.failure} />
      <ul aria-label="Activities of the day" className={styles.list}>
        {activities.map((activity) => (
          <li key={activity.id} className={styles.activity}>
            <ActivityCheckbox activity={activity} actions={actions} />
            <p
              className={styles.meta}
            >{`${purposeOf(activity, roles, goals)} · ${quadrantOf(activity)}`}</p>
          </li>
        ))}
      </ul>
    </>
  );
}
