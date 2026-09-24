import { type Editing, useEditing } from '../../shared/useEditing';
import type { RankedEntryCommands } from './rankedEntry';

export function useEntryEditing(revision: RankedEntryCommands['revision']): Editing {
  const editing = useEditing();
  return {
    ...editing,
    edit: (id) => {
      revision.reset();
      editing.edit(id);
    },
  };
}
