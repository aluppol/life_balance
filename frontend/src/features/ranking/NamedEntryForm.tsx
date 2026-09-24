import type { ReactNode } from 'react';
import type { NamedDetails } from '../../api/rankedCollection';
import type { Feedback } from '../../shared/ui/feedback';
import { Form, FormFooter } from '../../shared/ui/Form';
import { RequiredTextField } from '../../shared/ui/RequiredTextField';
import { TextAreaField } from '../../shared/ui/TextAreaField';

interface NamedEntryFormProps {
  readonly label: string;
  readonly submitLabel: string;
  readonly draft: NamedDetails;
  readonly feedback: Feedback;
  readonly onChange: (draft: NamedDetails) => void;
  readonly onSubmit: () => void;
  readonly children?: ReactNode;
}

export function NamedEntryForm(form: NamedEntryFormProps) {
  const { draft, feedback, onChange } = form;
  return (
    <Form label={form.label} onSubmit={form.onSubmit}>
      <RequiredTextField
        label="Name"
        value={draft.name}
        maxLength={100}
        error={feedback.fieldErrors.name}
        onChange={(name) => onChange({ ...draft, name })}
      />
      <TextAreaField
        label="Description"
        value={draft.description}
        maxLength={1000}
        rows={3}
        error={feedback.fieldErrors.description}
        onChange={(description) => onChange({ ...draft, description })}
      />
      <FormFooter submitLabel={form.submitLabel} feedback={feedback}>
        {form.children}
      </FormFooter>
    </Form>
  );
}
