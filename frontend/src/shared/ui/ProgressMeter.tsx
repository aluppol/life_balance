import { useId } from 'react';
import styles from './ProgressMeter.module.css';

interface ProgressMeterProps {
  readonly label: string;
  readonly completed: number;
  readonly planned: number;
}

export function ProgressMeter({ label, completed, planned }: ProgressMeterProps) {
  const labelId = useId();
  if (planned === 0) {
    return <p className={styles.caption}>{`${label}: none planned`}</p>;
  }
  return (
    <div className={styles.meter}>
      <p id={labelId} className={styles.caption}>
        {`${label}: ${String(completed)} of ${String(planned)} done`}
      </p>
      <progress aria-labelledby={labelId} value={completed} max={planned} />
    </div>
  );
}
