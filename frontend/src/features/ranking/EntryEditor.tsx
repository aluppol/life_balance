import { CancelButton } from '../../shared/ui/ActionButtons';
import { NamedEntryForm } from './NamedEntryForm';
import type { RankedEntry, RankedEntryCommands } from './rankedEntry';
import { useEntryRevision } from './useEntryDrafts';

interface EntryEditorProps {
  readonly entry: RankedEntry;
  readonly revision: RankedEntryCommands['revision'];
  readonly onDone: () => void;
}

export function EntryEditor({ entry, revision, onDone }: EntryEditorProps) {
  const editor = useEntryRevision(entry, revision, onDone);
  return (
    <NamedEntryForm
      label={`Edit ${entry.name}`}
      submitLabel="Save"
      draft={editor.draft}
      feedback={editor.feedback}
      onChange={editor.changeDraft}
      onSubmit={editor.submit}
    >
      <CancelButton onCancel={onDone} />
    </NamedEntryForm>
  );
}
