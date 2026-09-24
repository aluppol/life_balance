import buttons from '../../shared/ui/buttons.module.css';
import type { RankedEntry } from './rankedEntry';
import styles from './RankedEntries.module.css';
import type { RankedListActions } from './ranking';

interface MoveButtonsProps {
  readonly entry: RankedEntry;
  readonly isFirst: boolean;
  readonly isLast: boolean;
  readonly actions: RankedListActions;
}

export function MoveButtons({ entry, isFirst, isLast, actions }: MoveButtonsProps) {
  return (
    <div role="group" aria-label={`Rank of ${entry.name}`} className={styles.moveButtons}>
      <button
        type="button"
        className={buttons.icon}
        aria-label={`Move ${entry.name} up`}
        disabled={isFirst || actions.isBusy}
        onClick={() => actions.moveEarlier(entry.id)}
      >
        ↑
      </button>
      <button
        type="button"
        className={buttons.icon}
        aria-label={`Move ${entry.name} down`}
        disabled={isLast || actions.isBusy}
        onClick={() => actions.moveLater(entry.id)}
      >
        ↓
      </button>
    </div>
  );
}
