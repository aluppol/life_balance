import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { firstRoleId } from '../roles/lifeRoleChoices';
import { dayChoicesOf } from './activityDrafts';
import { ActivityForm } from './ActivityForm';
import { useActivityCreation } from './useActivityDrafts';

interface NewActivityProps {
  readonly monday: string;
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
}

export function NewActivity({ monday, roles, goals }: NewActivityProps) {
  const creation = useActivityCreation(monday, firstRoleId(roles), goals);
  return (
    <ActivityForm
      label="New activity"
      submitLabel="Plan activity"
      draft={creation.draft}
      roles={roles}
      goalChoices={creation.goalChoices}
      dayChoices={dayChoicesOf(monday)}
      feedback={creation.feedback}
      onChange={creation.changeDraft}
      onSubmit={creation.submit}
    />
  );
}
