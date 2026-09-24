import type { Goal } from '../../api/goals';
import type { GoalActions } from './useGoalActions';

interface GoalStatusButtonsProps {
  readonly goal: Goal;
  readonly actions: GoalActions;
}

interface GoalActionButtonProps {
  readonly verb: string;
  readonly goal: Goal;
  readonly isDisabled: boolean;
  readonly onAct: (goal: Goal) => void;
}

export function GoalStatusButtons({ goal, actions }: GoalStatusButtonsProps) {
  if (goal.status !== 'ACTIVE') {
    return (
      <GoalActionButton
        verb="Reopen"
        goal={goal}
        isDisabled={actions.isBusy}
        onAct={actions.reopen}
      />
    );
  }
  return (
    <>
      <GoalActionButton
        verb="Achieve"
        goal={goal}
        isDisabled={actions.isBusy}
        onAct={actions.achieve}
      />
      <GoalActionButton verb="Drop" goal={goal} isDisabled={actions.isBusy} onAct={actions.drop} />
    </>
  );
}

function GoalActionButton({ verb, goal, isDisabled, onAct }: GoalActionButtonProps) {
  return (
    <button
      type="button"
      aria-label={`${verb} ${goal.title}`}
      disabled={isDisabled}
      onClick={() => onAct(goal)}
    >
      {verb}
    </button>
  );
}
