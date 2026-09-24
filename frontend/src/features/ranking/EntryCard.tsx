import type { ReactNode } from 'react';
import type { RankedEntry } from './rankedEntry';
import styles from './RankedEntries.module.css';

interface EntryCardProps {
  readonly entry: RankedEntry;
  readonly badge: string | undefined;
  readonly children: ReactNode;
}

export function EntryCard({ entry, badge, children }: EntryCardProps) {
  return (
    <div className={styles.card}>
      <div className={styles.summary}>
        <div className={styles.heading}>
          <h3 className={styles.name}>{entry.name}</h3>
          <span className={styles.badge}>{badge}</span>
        </div>
        {entry.description === '' ? null : (
          <p className={styles.description}>{entry.description}</p>
        )}
      </div>
      <div className={styles.controls}>{children}</div>
    </div>
  );
}
