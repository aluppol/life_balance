import type { Activity } from '../../api/activities';
import { type ActivityContext, ActivityEntry } from './ActivityEntry';
import styles from './Week.module.css';

interface ActivityListProps {
  readonly label: string;
  readonly activities: readonly Activity[];
  readonly emptyMessage: string;
  readonly context: ActivityContext;
}

export function ActivityList({ label, activities, emptyMessage, context }: ActivityListProps) {
  if (activities.length === 0) {
    return <p className={styles.empty}>{emptyMessage}</p>;
  }
  return (
    <ul aria-label={label} className={styles.list}>
      {activities.map((activity) => (
        <li key={activity.id} className={styles.activity}>
          <ActivityEntry activity={activity} context={context} />
        </li>
      ))}
    </ul>
  );
}
