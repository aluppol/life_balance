import { useState } from 'react';
import type { Goal } from '../../api/goals';
import { feedbackOf, unconfirmedFeedbackOf } from '../common/mutationFeedback';
import { useReviseGoal, useSetGoal } from './goalCommands';
import { detailsOfGoalDraft, draftOfGoal, emptyGoalDraft } from './goalDrafts';

export function useGoalCreation(firstRoleId: string) {
  const setting = useSetGoal();
  const [draft, setDraft] = useState(() => emptyGoalDraft(firstRoleId));
  return {
    draft,
    changeDraft: setDraft,
    submit: () =>
      setting.mutate(detailsOfGoalDraft(draft), {
        onSuccess: () => setDraft(emptyGoalDraft(draft.roleId)),
      }),
    feedback: feedbackOf(setting, 'Goal added.'),
  };
}

export function useGoalRevision(goal: Goal, onRevised: () => void) {
  const revision = useReviseGoal();
  const [draft, setDraft] = useState(() => draftOfGoal(goal));
  return {
    draft,
    changeDraft: setDraft,
    submit: () =>
      revision.mutate(
        { id: goal.id, details: detailsOfGoalDraft(draft) },
        { onSuccess: onRevised },
      ),
    feedback: unconfirmedFeedbackOf(revision),
  };
}
