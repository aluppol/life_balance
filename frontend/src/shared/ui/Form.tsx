import type { ReactNode } from 'react';
import buttons from './buttons.module.css';
import type { Feedback } from './feedback';
import styles from './Form.module.css';
import { FailureMessage, StatusMessage } from './Messages';

interface FormProps {
  readonly label: string;
  readonly onSubmit: () => void;
  readonly children: ReactNode;
}

interface FormFooterProps {
  readonly submitLabel: string;
  readonly feedback: Feedback;
  readonly children?: ReactNode;
}

export function Form({ label, onSubmit, children }: FormProps) {
  return (
    <form
      aria-label={label}
      className={styles.form}
      onSubmit={(event) => {
        event.preventDefault();
        onSubmit();
      }}
    >
      {children}
    </form>
  );
}

export function FormFooter({ submitLabel, feedback, children }: FormFooterProps) {
  return (
    <div className={styles.footer}>
      <div className={styles.actions}>
        <button type="submit" className={buttons.primary} disabled={feedback.isSaving}>
          {submitLabel}
        </button>
        {children}
      </div>
      <FailureMessage message={feedback.failure} />
      <StatusMessage message={feedback.confirmation} />
    </div>
  );
}

export function FieldRow({ children }: { readonly children: ReactNode }) {
  return <div className={styles.row}>{children}</div>;
}
