import type { CoreValue } from '../../api/coreValues';
import type { Goal, GoalDetails } from '../../api/goals';
import { nullWhenEmpty } from '../../shared/text';
import type { Choice } from '../../shared/ui/choice';

export interface GoalDraft {
  readonly roleId: string;
  readonly title: string;
  readonly description: string;
  readonly dueOn: string;
  readonly valueIds: readonly string[];
}

export function emptyGoalDraft(roleId: string): GoalDraft {
  return { roleId, title: '', description: '', dueOn: '', valueIds: [] };
}

export function draftOfGoal(goal: Goal): GoalDraft {
  return {
    roleId: goal.roleId,
    title: goal.title,
    description: goal.description,
    dueOn: goal.dueOn ?? '',
    valueIds: goal.valueIds,
  };
}

export function detailsOfGoalDraft(draft: GoalDraft): GoalDetails {
  return { ...draft, dueOn: nullWhenEmpty(draft.dueOn) };
}

export function valueChoices(coreValues: readonly CoreValue[]): Choice<string>[] {
  return coreValues.map((value) => ({ value: value.id, label: value.name }));
}
