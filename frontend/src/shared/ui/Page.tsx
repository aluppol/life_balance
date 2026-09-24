import type { ReactNode } from 'react';
import { focusOnMount } from '../focus';
import styles from './Page.module.css';

interface PageProps {
  readonly title: string;
  readonly intro: string;
  readonly children?: ReactNode;
}

export function Page({ title, intro, children }: PageProps) {
  return (
    <div className={styles.page}>
      <title>{`${title} · Life Balance`}</title>
      <header className={styles.header}>
        <h1 ref={focusOnMount} tabIndex={-1} className={styles.title}>
          {title}
        </h1>
        <p className={styles.intro}>{intro}</p>
      </header>
      {children}
    </div>
  );
}
