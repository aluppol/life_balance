import type { WeeklyReviewDetails } from '../../api/weeklyReview';
import { CheckboxGroup } from '../../shared/ui/CheckboxGroup';
import type { Feedback } from '../../shared/ui/feedback';
import { Form, FormFooter } from '../../shared/ui/Form';
import { renewalChoices } from './renewal';
import { ReviewTextFields } from './ReviewTextFields';

interface ReviewFormProps {
  readonly draft: WeeklyReviewDetails;
  readonly feedback: Feedback;
  readonly onChange: (draft: WeeklyReviewDetails) => void;
  readonly onSubmit: () => void;
}

export function ReviewForm({ draft, feedback, onChange, onSubmit }: ReviewFormProps) {
  const change = (patch: Partial<WeeklyReviewDetails>) => onChange({ ...draft, ...patch });
  return (
    <Form label="Weekly review" onSubmit={onSubmit}>
      <ReviewTextFields draft={draft} errors={feedback.fieldErrors} onChange={change} />
      <CheckboxGroup
        legend="Which dimensions did you renew? (Habit 7: Sharpen the Saw)"
        choices={renewalChoices}
        selected={draft.renewedDimensions}
        onChange={(renewedDimensions) => change({ renewedDimensions })}
      />
      <FormFooter submitLabel="Save review" feedback={feedback} />
    </Form>
  );
}
