import { type ReactNode, useId } from 'react';
import styles from './Section.module.css';

interface SectionProps {
  readonly title: string;
  readonly children: ReactNode;
}

export function Section({ title, children }: SectionProps) {
  const headingId = useId();
  return (
    <section aria-labelledby={headingId} className={styles.section}>
      <h2 id={headingId} className={styles.title}>
        {title}
      </h2>
      {children}
    </section>
  );
}

export function Subsection({ title, children }: SectionProps) {
  const headingId = useId();
  return (
    <section aria-labelledby={headingId} className={styles.subsection}>
      <h3 id={headingId} className={styles.subtitle}>
        {title}
      </h3>
      {children}
    </section>
  );
}
