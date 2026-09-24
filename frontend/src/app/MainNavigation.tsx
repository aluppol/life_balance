import { NavLink } from 'react-router';
import styles from './AppShell.module.css';

const destinations = [
  { path: '/', label: 'Compass' },
  { path: '/mission', label: 'Mission' },
  { path: '/values', label: 'Values' },
  { path: '/roles', label: 'Roles' },
  { path: '/goals', label: 'Goals' },
  { path: '/week', label: 'Week' },
  { path: '/day', label: 'Day' },
  { path: '/review', label: 'Review' },
] as const;

export function MainNavigation() {
  return (
    <nav aria-label="Main" className={styles.navigation}>
      <ul className={styles.navigationList}>
        {destinations.map((destination) => (
          <li key={destination.path}>
            <NavLink to={destination.path}>{destination.label}</NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
