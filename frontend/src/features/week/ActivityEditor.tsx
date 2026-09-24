import type { Activity } from '../../api/activities';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { CancelButton } from '../../shared/ui/ActionButtons';
import { dayChoicesOf } from './activityDrafts';
import { ActivityForm } from './ActivityForm';
import { useActivityRevision } from './useActivityDrafts';

interface ActivityEditorProps {
  readonly activity: Activity;
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
  readonly onDone: () => void;
}

export function ActivityEditor({ activity, roles, goals, onDone }: ActivityEditorProps) {
  const editor = useActivityRevision(activity, goals, onDone);
  return (
    <ActivityForm
      label={`Edit ${activity.title}`}
      submitLabel="Save activity"
      draft={editor.draft}
      roles={roles}
      goalChoices={editor.goalChoices}
      dayChoices={dayChoicesOf(activity.weekStart)}
      feedback={editor.feedback}
      onChange={editor.changeDraft}
      onSubmit={editor.submit}
    >
      <CancelButton onCancel={onDone} />
    </ActivityForm>
  );
}
