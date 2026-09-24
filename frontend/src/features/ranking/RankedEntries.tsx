import { FailureMessage } from '../../shared/ui/Messages';
import type { RankedEntry, RankedEntryCommands } from './rankedEntry';
import styles from './RankedEntries.module.css';
import { RankedEntryView } from './RankedEntryView';
import { rankedListActionsOf } from './ranking';
import { useEntryEditing } from './useEntryEditing';

interface RankedEntriesProps<Entry extends RankedEntry> {
  readonly label: string;
  readonly emptyMessage: string;
  readonly entries: readonly Entry[];
  readonly commands: RankedEntryCommands;
  readonly badgeOf?: (entry: Entry) => string | undefined;
  readonly isRemovable?: (entry: Entry) => boolean;
}

export function RankedEntries<Entry extends RankedEntry>(list: RankedEntriesProps<Entry>) {
  const { entries, commands } = list;
  const controls = {
    revision: commands.revision,
    editing: useEntryEditing(commands.revision),
    actions: rankedListActionsOf(entries, commands),
  };
  if (entries.length === 0) {
    return <p className={styles.empty}>{list.emptyMessage}</p>;
  }
  return (
    <div className={styles.entries}>
      <ol aria-label={list.label} className={styles.list}>
        {entries.map((entry, index) => (
          <li key={entry.id} className={styles.entry}>
            <RankedEntryView
              entry={entry}
              badge={list.badgeOf?.(entry)}
              isRemovable={list.isRemovable?.(entry) ?? true}
              placement={{ isFirst: index === 0, isLast: index === entries.length - 1 }}
              controls={controls}
            />
          </li>
        ))}
      </ol>
      <FailureMessage message={controls.actions.failure} />
    </div>
  );
}
