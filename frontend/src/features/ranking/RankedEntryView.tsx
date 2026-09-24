import type { Editing } from '../../shared/useEditing';
import { EntryCard } from './EntryCard';
import { EntryControls } from './EntryControls';
import { EntryEditor } from './EntryEditor';
import type { RankedEntry, RankedEntryCommands } from './rankedEntry';
import type { RankedListActions } from './ranking';

interface RankedEntryViewProps {
  readonly entry: RankedEntry;
  readonly badge: string | undefined;
  readonly isRemovable: boolean;
  readonly placement: { readonly isFirst: boolean; readonly isLast: boolean };
  readonly controls: {
    readonly revision: RankedEntryCommands['revision'];
    readonly editing: Editing;
    readonly actions: RankedListActions;
  };
}

export function RankedEntryView({
  entry,
  badge,
  isRemovable,
  placement,
  controls,
}: RankedEntryViewProps) {
  const { revision, editing, actions } = controls;
  if (editing.editedId === entry.id) {
    return <EntryEditor entry={entry} revision={revision} onDone={editing.finishEditing} />;
  }
  return (
    <EntryCard entry={entry} badge={badge}>
      <EntryControls
        entry={entry}
        placement={placement}
        isRemovable={isRemovable}
        editing={editing}
        actions={actions}
      />
    </EntryCard>
  );
}
