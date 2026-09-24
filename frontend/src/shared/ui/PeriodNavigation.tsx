import { Link } from 'react-router';
import styles from './PeriodNavigation.module.css';

interface PeriodLink {
  readonly to: string;
  readonly label: string;
}

interface PeriodNavigationProps {
  readonly label: string;
  readonly previous: PeriodLink;
  readonly anchor: PeriodLink & { readonly isShown: boolean };
  readonly next: PeriodLink;
}

export function PeriodNavigation({ label, previous, anchor, next }: PeriodNavigationProps) {
  return (
    <nav aria-label={label} className={styles.navigation}>
      <Link to={previous.to} className={styles.link}>
        <span aria-hidden="true">← </span>
        {previous.label}
      </Link>
      <Link
        to={anchor.to}
        className={styles.link}
        aria-current={anchor.isShown ? 'page' : undefined}
      >
        {anchor.label}
      </Link>
      <Link to={next.to} className={styles.link}>
        {next.label}
        <span aria-hidden="true"> →</span>
      </Link>
    </nav>
  );
}
