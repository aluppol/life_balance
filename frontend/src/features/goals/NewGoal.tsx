import type { CoreValue } from '../../api/coreValues';
import type { LifeRole } from '../../api/lifeRoles';
import { firstRoleId } from '../roles/lifeRoleChoices';
import { GoalForm } from './GoalForm';
import { useGoalCreation } from './useGoalDrafts';

interface NewGoalProps {
  readonly roles: readonly LifeRole[];
  readonly coreValues: readonly CoreValue[];
}

export function NewGoal({ roles, coreValues }: NewGoalProps) {
  const creation = useGoalCreation(firstRoleId(roles));
  return (
    <GoalForm
      label="New goal"
      submitLabel="Add goal"
      draft={creation.draft}
      roles={roles}
      coreValues={coreValues}
      feedback={creation.feedback}
      onChange={creation.changeDraft}
      onSubmit={creation.submit}
    />
  );
}
