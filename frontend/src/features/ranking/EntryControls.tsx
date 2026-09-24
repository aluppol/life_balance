import { DeleteButton, EditButton } from '../../shared/ui/ActionButtons';
import type { Editing } from '../../shared/useEditing';
import { MoveButtons } from './MoveButtons';
import type { RankedEntry } from './rankedEntry';
import type { RankedListActions } from './ranking';

interface EntryControlsProps {
  readonly entry: RankedEntry;
  readonly placement: { readonly isFirst: boolean; readonly isLast: boolean };
  readonly isRemovable: boolean;
  readonly editing: Editing;
  readonly actions: RankedListActions;
}

export function EntryControls({
  entry,
  placement,
  isRemovable,
  editing,
  actions,
}: EntryControlsProps) {
  return (
    <>
      <MoveButtons entry={entry} {...placement} actions={actions} />
      <EditButton name={entry.name} onEdit={() => editing.edit(entry.id)} />
      {isRemovable ? (
        <DeleteButton
          name={entry.name}
          isDisabled={actions.isBusy}
          onDelete={() => actions.remove(entry.id)}
        />
      ) : null}
    </>
  );
}
