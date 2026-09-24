import type { SignedInPerson } from '../../api/signedInPerson';
import styles from './Account.module.css';

interface AccountStatusProps {
  readonly person: SignedInPerson | undefined;
}

export function AccountStatus({ person }: AccountStatusProps) {
  return (
    <div className={styles.status}>
      {person === undefined ? null : (
        <p className={styles.name}>
          <span className={styles.visuallyHidden}>Signed in as </span>
          {person.displayName}
        </p>
      )}
      <a href="/oauth2/sign_out" className={styles.signOut}>
        Sign out
      </a>
    </div>
  );
}
