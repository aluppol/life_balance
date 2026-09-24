import type { ReactNode } from 'react';
import type { CoreValue } from '../../api/coreValues';
import type { LifeRole } from '../../api/lifeRoles';
import type { Feedback } from '../../shared/ui/feedback';
import { Form, FormFooter } from '../../shared/ui/Form';
import type { GoalDraft } from './goalDrafts';
import { GoalDetailFields, GoalPurposeFields, GoalValueField } from './GoalFields';

interface GoalFormProps {
  readonly label: string;
  readonly submitLabel: string;
  readonly draft: GoalDraft;
  readonly roles: readonly LifeRole[];
  readonly coreValues: readonly CoreValue[];
  readonly feedback: Feedback;
  readonly onChange: (draft: GoalDraft) => void;
  readonly onSubmit: () => void;
  readonly children?: ReactNode;
}

export function GoalForm(form: GoalFormProps) {
  const { draft, feedback } = form;
  const fields = {
    draft,
    errors: feedback.fieldErrors,
    onChange: (patch: Partial<GoalDraft>) => form.onChange({ ...draft, ...patch }),
  };
  return (
    <Form label={form.label} onSubmit={form.onSubmit}>
      <GoalPurposeFields fields={fields} roles={form.roles} />
      <GoalDetailFields fields={fields} />
      <GoalValueField fields={fields} coreValues={form.coreValues} />
      <FormFooter submitLabel={form.submitLabel} feedback={feedback}>
        {form.children}
      </FormFooter>
    </Form>
  );
}
