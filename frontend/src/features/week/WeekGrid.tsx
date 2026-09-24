import { useId } from 'react';
import { Link } from 'react-router';
import type { Activity } from '../../api/activities';
import { formatShortDay, weekDays } from '../../shared/calendar';
import { dayPath } from '../common/paths';
import styles from './Week.module.css';
import { activitiesOn, unscheduledActivities } from './activityFacts';

interface WeekGridProps {
  readonly monday: string;
  readonly activities: readonly Activity[];
}

export function WeekGrid({ monday, activities }: WeekGridProps) {
  return (
    <div className={styles.grid}>
      {weekDays(monday).map((day) => (
        <GlanceDay key={day} day={day} activities={activitiesOn(activities, day)} />
      ))}
      <GlanceUnscheduled activities={unscheduledActivities(activities)} />
    </div>
  );
}

function GlanceDay({
  day,
  activities,
}: {
  readonly day: string;
  readonly activities: readonly Activity[];
}) {
  const headingId = useId();
  return (
    <section aria-labelledby={headingId} className={styles.day}>
      <h3 id={headingId} className={styles.dayTitle}>
        <Link to={dayPath(day)}>{formatShortDay(day)}</Link>
      </h3>
      <GlanceList activities={activities} />
    </section>
  );
}

function GlanceUnscheduled({ activities }: { readonly activities: readonly Activity[] }) {
  const headingId = useId();
  return (
    <section aria-labelledby={headingId} className={styles.day}>
      <h3 id={headingId} className={styles.dayTitle}>
        Unscheduled
      </h3>
      <GlanceList activities={activities} />
    </section>
  );
}

function GlanceList({ activities }: { readonly activities: readonly Activity[] }) {
  if (activities.length === 0) {
    return <p className={styles.free}>Nothing planned</p>;
  }
  return (
    <ul className={styles.glance}>
      {activities.map((activity) => (
        <li key={activity.id} className={styles.glanceActivity}>
          {activity.title}
          {activity.isCompleted ? <span className={styles.done}> Done</span> : null}
        </li>
      ))}
    </ul>
  );
}
