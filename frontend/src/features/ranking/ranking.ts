import { moveEarlier, moveLater } from '../../shared/ordering';
import { failureOf, resetEach } from '../common/mutationFeedback';
import type { RankedEntry, RankedEntryCommands } from './rankedEntry';

export interface RankedListActions {
  readonly moveEarlier: (id: string) => void;
  readonly moveLater: (id: string) => void;
  readonly remove: (id: string) => void;
  readonly isBusy: boolean;
  readonly failure: string;
}

export function rankedListActionsOf(
  entries: readonly RankedEntry[],
  { reorder, removal }: RankedEntryCommands,
): RankedListActions {
  const ids = entries.map((entry) => entry.id);
  const reorderTo = (order: readonly string[]) => {
    resetEach([removal]);
    reorder.mutate(order);
  };
  return {
    moveEarlier: (id) => reorderTo(moveEarlier(ids, id)),
    moveLater: (id) => reorderTo(moveLater(ids, id)),
    remove: (id) => {
      resetEach([reorder]);
      removal.mutate(id);
    },
    isBusy: reorder.isPending || removal.isPending,
    failure: failureOf([reorder, removal]),
  };
}
