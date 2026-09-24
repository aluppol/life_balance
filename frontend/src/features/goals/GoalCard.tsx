import type { ReactNode } from 'react';
import type { Goal } from '../../api/goals';
import { formatCalendarDate } from '../../shared/calendar';
import { goalStatusLabels } from './goalStatuses';
import styles from './Goals.module.css';

interface GoalCardProps {
  readonly goal: Goal;
  readonly valueNames: readonly string[];
  readonly children: ReactNode;
}

export function GoalCard({ goal, valueNames, children }: GoalCardProps) {
  return (
    <div className={styles.card}>
      <div className={styles.heading}>
        <h3 className={styles.title}>{goal.title}</h3>
        <span className={styles.status}>{goalStatusLabels[goal.status]}</span>
      </div>
      {goal.description === '' ? null : <p className={styles.description}>{goal.description}</p>}
      <p className={styles.facts}>
        {goal.dueOn === null ? 'No due date' : `Due ${formatCalendarDate(goal.dueOn)}`}
        {valueNames.length === 0 ? null : ` · Serves ${valueNames.join(', ')}`}
      </p>
      <div className={styles.actions}>{children}</div>
    </div>
  );
}
