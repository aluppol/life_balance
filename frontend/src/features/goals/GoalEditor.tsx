import type { CoreValue } from '../../api/coreValues';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { CancelButton } from '../../shared/ui/ActionButtons';
import { GoalForm } from './GoalForm';
import { useGoalRevision } from './useGoalDrafts';

interface GoalEditorProps {
  readonly goal: Goal;
  readonly roles: readonly LifeRole[];
  readonly coreValues: readonly CoreValue[];
  readonly onDone: () => void;
}

export function GoalEditor({ goal, roles, coreValues, onDone }: GoalEditorProps) {
  const editor = useGoalRevision(goal, onDone);
  return (
    <GoalForm
      label={`Edit ${goal.title}`}
      submitLabel="Save goal"
      draft={editor.draft}
      roles={roles}
      coreValues={coreValues}
      feedback={editor.feedback}
      onChange={editor.changeDraft}
      onSubmit={editor.submit}
    >
      <CancelButton onCancel={onDone} />
    </GoalForm>
  );
}
