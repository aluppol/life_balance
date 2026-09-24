import type { ReactNode } from 'react';
import { errorIdOf, hintIdOf } from './fieldDescriptions';
import styles from './Fields.module.css';

interface FieldFrameProps {
  readonly inputId: string;
  readonly label: string;
  readonly hint?: string | undefined;
  readonly error?: string | undefined;
  readonly children: ReactNode;
}

export function FieldFrame({ inputId, label, hint, error, children }: FieldFrameProps) {
  return (
    <div className={styles.field}>
      <label htmlFor={inputId} className={styles.label}>
        {label}
      </label>
      {children}
      {hint === undefined ? null : (
        <p id={hintIdOf(inputId)} className={styles.hint}>
          {hint}
        </p>
      )}
      {error === undefined ? null : (
        <p id={errorIdOf(inputId)} className={styles.error}>
          {error}
        </p>
      )}
    </div>
  );
}
