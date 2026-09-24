import type { Goal, GoalStatus } from '../../api/goals';
import { failureOf, resetEach } from '../common/mutationFeedback';
import { useChangeGoalStatus, useRemoveGoal } from './goalCommands';

export interface GoalActions {
  readonly achieve: (goal: Goal) => void;
  readonly drop: (goal: Goal) => void;
  readonly reopen: (goal: Goal) => void;
  readonly remove: (goal: Goal) => void;
  readonly isBusy: boolean;
  readonly failure: string;
}

export function useGoalActions(): GoalActions {
  const statusChange = useChangeGoalStatus();
  const removal = useRemoveGoal();
  const changeStatus = (goal: Goal, status: GoalStatus) => {
    resetEach([removal]);
    statusChange.mutate({ id: goal.id, status });
  };
  return {
    achieve: (goal) => changeStatus(goal, 'ACHIEVED'),
    drop: (goal) => changeStatus(goal, 'DROPPED'),
    reopen: (goal) => changeStatus(goal, 'ACTIVE'),
    remove: (goal) => {
      resetEach([statusChange]);
      removal.mutate(goal.id);
    },
    isBusy: statusChange.isPending || removal.isPending,
    failure: failureOf([statusChange, removal]),
  };
}
