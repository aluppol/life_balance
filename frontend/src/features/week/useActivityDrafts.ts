import { useState } from 'react';
import type { Activity } from '../../api/activities';
import type { Goal } from '../../api/goals';
import { feedbackOf, unconfirmedFeedbackOf } from '../common/mutationFeedback';
import { usePlanActivity, useReviseActivity } from './activityCommands';
import {
  type ActivityDraft,
  detailsOfActivityDraft,
  draftOfActivity,
  emptyActivityDraft,
  goalChoicesFor,
  withConsistentGoal,
} from './activityDrafts';

export function useActivityCreation(
  weekStart: string,
  firstRoleId: string,
  goals: readonly Goal[],
) {
  const planning = usePlanActivity(weekStart);
  const [draft, setDraft] = useState(() => emptyActivityDraft(firstRoleId));
  return {
    draft,
    goalChoices: goalChoicesFor(goals, draft.roleId, null),
    changeDraft: (next: ActivityDraft) => setDraft(withConsistentGoal(next, goals)),
    submit: () =>
      planning.mutate(detailsOfActivityDraft(draft), {
        onSuccess: () => setDraft(emptyActivityDraft(draft.roleId)),
      }),
    feedback: feedbackOf(planning, 'Activity planned.'),
  };
}

export function useActivityRevision(
  activity: Activity,
  goals: readonly Goal[],
  onRevised: () => void,
) {
  const revision = useReviseActivity(activity.weekStart);
  const [draft, setDraft] = useState(() => draftOfActivity(activity));
  return {
    draft,
    goalChoices: goalChoicesFor(goals, draft.roleId, activity.goalId),
    changeDraft: (next: ActivityDraft) => setDraft(withConsistentGoal(next, goals)),
    submit: () =>
      revision.mutate(
        { id: activity.id, details: detailsOfActivityDraft(draft) },
        { onSuccess: onRevised },
      ),
    feedback: unconfirmedFeedbackOf(revision),
  };
}
