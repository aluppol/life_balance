import type { Activity } from '../../api/activities';
import { failureOf, resetEach } from '../common/mutationFeedback';
import { useCompleteActivity, useRemoveActivity, useReopenActivity } from './activityCommands';

export interface ActivityActions {
  readonly complete: (activity: Activity) => void;
  readonly reopen: (activity: Activity) => void;
  readonly remove: (activity: Activity) => void;
  readonly isBusy: boolean;
  readonly failure: string;
}

export function useActivityActions(weekStart: string): ActivityActions {
  const completion = useCompleteActivity(weekStart);
  const reopening = useReopenActivity(weekStart);
  const removal = useRemoveActivity(weekStart);
  const mutations = [completion, reopening, removal];
  const run = (mutation: (typeof mutations)[number], activity: Activity) => {
    resetEach(mutations);
    mutation.mutate(activity.id);
  };
  return {
    complete: (activity) => run(completion, activity),
    reopen: (activity) => run(reopening, activity),
    remove: (activity) => run(removal, activity),
    isBusy: mutations.some((mutation) => mutation.isPending),
    failure: failureOf(mutations),
  };
}
