import styles from './Messages.module.css';

interface MessageProps {
  readonly message: string;
}

interface LoadFailureProps extends MessageProps {
  readonly onRetry: () => void;
}

export function LoadingMessage() {
  return (
    <p role="status" className={styles.loading}>
      Loading…
    </p>
  );
}

export function FailureMessage({ message }: MessageProps) {
  if (message === '') {
    return null;
  }
  return (
    <p role="alert" className={styles.failure}>
      {message}
    </p>
  );
}

export function StatusMessage({ message }: MessageProps) {
  return (
    <p role="status" className={styles.status}>
      {message}
    </p>
  );
}

export function LoadFailure({ message, onRetry }: LoadFailureProps) {
  return (
    <div role="alert" className={styles.failure}>
      <p className={styles.failureText}>{message}</p>
      <button type="button" onClick={onRetry}>
        Try again
      </button>
    </div>
  );
}
