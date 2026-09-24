import type { LifeRole } from '../../api/lifeRoles';
import type { Choice } from '../../shared/ui/choice';
import type { FieldGroup } from '../../shared/ui/fieldGroup';
import { FieldRow } from '../../shared/ui/Form';
import { RequiredTextField } from '../../shared/ui/RequiredTextField';
import { SelectField } from '../../shared/ui/SelectField';
import { roleChoices } from '../roles/lifeRoleChoices';
import type { ActivityDraft } from './activityDrafts';
import { quadrantChoices } from './quadrants';

interface ActivityFieldsProps {
  readonly fields: FieldGroup<ActivityDraft>;
}

interface PurposeFieldsProps extends ActivityFieldsProps {
  readonly roles: readonly LifeRole[];
  readonly goalChoices: readonly Choice<string>[];
}

interface TimingFieldsProps extends ActivityFieldsProps {
  readonly dayChoices: readonly Choice<string>[];
}

export function ActivityTitleField({ fields: { draft, errors, onChange } }: ActivityFieldsProps) {
  return (
    <RequiredTextField
      label="Title"
      value={draft.title}
      maxLength={200}
      error={errors.title}
      onChange={(title) => onChange({ title })}
    />
  );
}

export function ActivityPurposeFields({ fields, roles, goalChoices }: PurposeFieldsProps) {
  const { draft, errors, onChange } = fields;
  return (
    <FieldRow>
      <SelectField
        label="Role"
        value={draft.roleId}
        choices={roleChoices(roles)}
        error={errors.roleId}
        onChange={(roleId) => onChange({ roleId })}
      />
      <SelectField
        label="Goal"
        value={draft.goalId}
        choices={goalChoices}
        error={errors.goalId}
        onChange={(goalId) => onChange({ goalId })}
      />
    </FieldRow>
  );
}

export function ActivityTimingFields({ fields, dayChoices }: TimingFieldsProps) {
  const { draft, errors, onChange } = fields;
  return (
    <FieldRow>
      <SelectField
        label="Quadrant"
        value={draft.quadrant}
        choices={quadrantChoices}
        error={errors.quadrant}
        onChange={(quadrant) => onChange({ quadrant })}
      />
      <SelectField
        label="Day"
        value={draft.scheduledOn}
        choices={dayChoices}
        error={errors.scheduledOn}
        onChange={(scheduledOn) => onChange({ scheduledOn })}
      />
    </FieldRow>
  );
}
