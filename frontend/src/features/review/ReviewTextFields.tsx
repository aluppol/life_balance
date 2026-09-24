import type { WeeklyReviewDetails } from '../../api/weeklyReview';
import { TextAreaField } from '../../shared/ui/TextAreaField';

const maximumLength = 4000;

interface ReviewTextFieldsProps {
  readonly draft: WeeklyReviewDetails;
  readonly errors: Readonly<Record<string, string>>;
  readonly onChange: (patch: Partial<WeeklyReviewDetails>) => void;
}

export function ReviewTextFields({ draft, errors, onChange }: ReviewTextFieldsProps) {
  return (
    <>
      <TextAreaField
        label="What did you accomplish?"
        value={draft.accomplishments}
        maxLength={maximumLength}
        rows={4}
        error={errors.accomplishments}
        onChange={(accomplishments) => onChange({ accomplishments })}
      />
      <TextAreaField
        label="What did you learn?"
        value={draft.lessons}
        maxLength={maximumLength}
        rows={4}
        error={errors.lessons}
        onChange={(lessons) => onChange({ lessons })}
      />
    </>
  );
}
