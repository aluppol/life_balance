import type { Feedback } from '../../shared/ui/feedback';
import { Form, FormFooter } from '../../shared/ui/Form';
import { TextAreaField } from '../../shared/ui/TextAreaField';

const maximumLength = 4000;

interface MissionFormProps {
  readonly text: string;
  readonly feedback: Feedback;
  readonly onChange: (text: string) => void;
  readonly onSubmit: () => void;
}

export function MissionForm({ text, feedback, onChange, onSubmit }: MissionFormProps) {
  return (
    <Form label="Mission statement" onSubmit={onSubmit}>
      <TextAreaField
        label="Your mission statement"
        value={text}
        maxLength={maximumLength}
        rows={10}
        hint={`${String(text.length)} of ${String(maximumLength)} characters`}
        error={feedback.fieldErrors.text}
        onChange={onChange}
      />
      <FormFooter submitLabel="Save mission" feedback={feedback} />
    </Form>
  );
}
