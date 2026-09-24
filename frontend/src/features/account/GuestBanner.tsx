import styles from './Account.module.css';

export function GuestBanner() {
  return (
    <aside aria-label="Demo account" className={styles.guestBanner}>
      <p className={styles.guestText}>
        You are exploring a shared demo account. Feel free to change anything — it resets every
        night.
      </p>
    </aside>
  );
}
