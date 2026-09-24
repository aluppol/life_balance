import { Link, Outlet } from 'react-router';
import { useSignedInPerson } from '../features/account/accountQueries';
import { AccountStatus } from '../features/account/AccountStatus';
import { GuestBanner } from '../features/account/GuestBanner';
import styles from './AppShell.module.css';
import { MainNavigation } from './MainNavigation';

export function AppShell() {
  const person = useSignedInPerson();
  return (
    <div className={styles.shell}>
      <a href="#main-content" className={styles.skipLink}>
        Skip to content
      </a>
      <header className={styles.header}>
        <div className={styles.bar}>
          <Link to="/" className={styles.brand}>
            Life Balance
          </Link>
          <MainNavigation />
          <div className={styles.account}>
            <AccountStatus person={person.data} />
          </div>
        </div>
      </header>
      {person.data?.isGuest === true ? <GuestBanner /> : null}
      <main id="main-content" tabIndex={-1} className={styles.main}>
        <Outlet />
      </main>
    </div>
  );
}
