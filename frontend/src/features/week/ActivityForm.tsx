import type { ReactNode } from 'react';
import type { LifeRole } from '../../api/lifeRoles';
import type { Choice } from '../../shared/ui/choice';
import type { Feedback } from '../../shared/ui/feedback';
import { Form, FormFooter } from '../../shared/ui/Form';
import type { ActivityDraft } from './activityDrafts';
import { ActivityPurposeFields, ActivityTimingFields, ActivityTitleField } from './ActivityFields';

interface ActivityFormProps {
  readonly label: string;
  readonly submitLabel: string;
  readonly draft: ActivityDraft;
  readonly roles: readonly LifeRole[];
  readonly goalChoices: readonly Choice<string>[];
  readonly dayChoices: readonly Choice<string>[];
  readonly feedback: Feedback;
  readonly onChange: (draft: ActivityDraft) => void;
  readonly onSubmit: () => void;
  readonly children?: ReactNode;
}

export function ActivityForm(form: ActivityFormProps) {
  const { draft, feedback } = form;
  const fields = {
    draft,
    errors: feedback.fieldErrors,
    onChange: (patch: Partial<ActivityDraft>) => form.onChange({ ...draft, ...patch }),
  };
  return (
    <Form label={form.label} onSubmit={form.onSubmit}>
      <ActivityTitleField fields={fields} />
      <ActivityPurposeFields fields={fields} roles={form.roles} goalChoices={form.goalChoices} />
      <ActivityTimingFields fields={fields} dayChoices={form.dayChoices} />
      <FormFooter submitLabel={form.submitLabel} feedback={feedback}>
        {form.children}
      </FormFooter>
    </Form>
  );
}
