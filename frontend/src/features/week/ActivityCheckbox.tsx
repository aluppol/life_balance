import { useId } from 'react';
import type { Activity } from '../../api/activities';
import type { ActivityActions } from './useActivityActions';
import styles from './Week.module.css';

interface ActivityCheckboxProps {
  readonly activity: Activity;
  readonly actions: ActivityActions;
}

export function ActivityCheckbox({ activity, actions }: ActivityCheckboxProps) {
  const inputId = useId();
  return (
    <div className={styles.check}>
      <input
        id={inputId}
        type="checkbox"
        checked={activity.isCompleted}
        disabled={actions.isBusy}
        onChange={() =>
          activity.isCompleted ? actions.reopen(activity) : actions.complete(activity)
        }
      />
      <label htmlFor={inputId} className={styles.title}>
        {activity.title}
      </label>
    </div>
  );
}
