import type { CoreValue } from '../../api/coreValues';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { DeleteButton, EditButton } from '../../shared/ui/ActionButtons';
import type { Editing } from '../../shared/useEditing';
import { valueNamesOf } from './goalBoard';
import { GoalCard } from './GoalCard';
import { GoalEditor } from './GoalEditor';
import { GoalStatusButtons } from './GoalStatusButtons';
import type { GoalActions } from './useGoalActions';

export interface GoalContext {
  readonly roles: readonly LifeRole[];
  readonly coreValues: readonly CoreValue[];
  readonly editing: Editing;
  readonly actions: GoalActions;
}

interface GoalEntryProps {
  readonly goal: Goal;
  readonly context: GoalContext;
}

export function GoalEntry({ goal, context }: GoalEntryProps) {
  const { roles, coreValues, editing, actions } = context;
  if (editing.editedId === goal.id) {
    return (
      <GoalEditor
        goal={goal}
        roles={roles}
        coreValues={coreValues}
        onDone={editing.finishEditing}
      />
    );
  }
  return (
    <GoalCard goal={goal} valueNames={valueNamesOf(goal, coreValues)}>
      <GoalStatusButtons goal={goal} actions={actions} />
      <EditButton name={goal.title} onEdit={() => editing.edit(goal.id)} />
      <DeleteButton
        name={goal.title}
        isDisabled={actions.isBusy}
        onDelete={() => actions.remove(goal)}
      />
    </GoalCard>
  );
}
