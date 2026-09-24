import type { Activity } from '../../api/activities';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { DeleteButton, EditButton } from '../../shared/ui/ActionButtons';
import type { Editing } from '../../shared/useEditing';
import { ActivityCheckbox } from './ActivityCheckbox';
import { ActivityEditor } from './ActivityEditor';
import { purposeOf, scheduleOf } from './activityFacts';
import type { ActivityActions } from './useActivityActions';
import styles from './Week.module.css';

export interface ActivityContext {
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
  readonly editing: Editing;
  readonly actions: ActivityActions;
}

interface ActivityEntryProps {
  readonly activity: Activity;
  readonly context: ActivityContext;
}

export function ActivityEntry({ activity, context }: ActivityEntryProps) {
  const { roles, goals, editing, actions } = context;
  if (editing.editedId === activity.id) {
    return (
      <ActivityEditor
        activity={activity}
        roles={roles}
        goals={goals}
        onDone={editing.finishEditing}
      />
    );
  }
  return (
    <>
      <ActivityCheckbox activity={activity} actions={actions} />
      <p
        className={styles.meta}
      >{`${purposeOf(activity, roles, goals)} · ${scheduleOf(activity)}`}</p>
      <div className={styles.buttons}>
        <EditButton name={activity.title} onEdit={() => editing.edit(activity.id)} />
        <DeleteButton
          name={activity.title}
          isDisabled={actions.isBusy}
          onDelete={() => actions.remove(activity)}
        />
      </div>
    </>
  );
}
