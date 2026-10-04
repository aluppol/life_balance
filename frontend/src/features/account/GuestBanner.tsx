import styles from './Account.module.css';

export function GuestBanner() {
  return (
    <aside aria-label="Demo account" className={styles.guestBanner}>
      <p className={styles.guestText}>
        You are exploring a demo workspace of your own. Feel free to change anything — no one else
        sees it, and it resets every night.
      </p>
    </aside>
  );
}
