import type { CoreValue } from '../../api/coreValues';
import type { LifeRole } from '../../api/lifeRoles';
import { CheckboxGroup } from '../../shared/ui/CheckboxGroup';
import { DateField } from '../../shared/ui/DateField';
import type { FieldGroup } from '../../shared/ui/fieldGroup';
import { RequiredTextField } from '../../shared/ui/RequiredTextField';
import { SelectField } from '../../shared/ui/SelectField';
import { TextAreaField } from '../../shared/ui/TextAreaField';
import { roleChoices } from '../roles/lifeRoleChoices';
import { type GoalDraft, valueChoices } from './goalDrafts';

interface GoalFieldsProps {
  readonly fields: FieldGroup<GoalDraft>;
}

interface GoalPurposeFieldsProps extends GoalFieldsProps {
  readonly roles: readonly LifeRole[];
}

interface GoalValueFieldProps extends GoalFieldsProps {
  readonly coreValues: readonly CoreValue[];
}

export function GoalPurposeFields({ fields, roles }: GoalPurposeFieldsProps) {
  const { draft, errors, onChange } = fields;
  return (
    <>
      <SelectField
        label="Role"
        value={draft.roleId}
        choices={roleChoices(roles)}
        error={errors.roleId}
        onChange={(roleId) => onChange({ roleId })}
      />
      <RequiredTextField
        label="Title"
        value={draft.title}
        maxLength={200}
        error={errors.title}
        onChange={(title) => onChange({ title })}
      />
    </>
  );
}

export function GoalDetailFields({ fields: { draft, errors, onChange } }: GoalFieldsProps) {
  return (
    <>
      <TextAreaField
        label="Description"
        value={draft.description}
        maxLength={2000}
        rows={3}
        error={errors.description}
        onChange={(description) => onChange({ description })}
      />
      <DateField
        label="Due date (optional)"
        value={draft.dueOn}
        error={errors.dueOn}
        onChange={(dueOn) => onChange({ dueOn })}
      />
    </>
  );
}

export function GoalValueField({ fields: { draft, onChange }, coreValues }: GoalValueFieldProps) {
  return (
    <CheckboxGroup
      legend="Values it serves"
      choices={valueChoices(coreValues)}
      selected={draft.valueIds}
      onChange={(valueIds) => onChange({ valueIds })}
    />
  );
}
