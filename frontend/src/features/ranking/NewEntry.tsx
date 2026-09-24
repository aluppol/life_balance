import type { UseMutationResult } from '@tanstack/react-query';
import type { NamedDetails } from '../../api/rankedCollection';
import { NamedEntryForm } from './NamedEntryForm';
import { useEntryCreation } from './useEntryDrafts';

interface NewEntryProps {
  readonly label: string;
  readonly submitLabel: string;
  readonly confirmation: string;
  readonly addition: UseMutationResult<void, Error, NamedDetails>;
}

export function NewEntry({ label, submitLabel, confirmation, addition }: NewEntryProps) {
  const creation = useEntryCreation(addition, confirmation);
  return (
    <NamedEntryForm
      label={label}
      submitLabel={submitLabel}
      draft={creation.draft}
      feedback={creation.feedback}
      onChange={creation.changeDraft}
      onSubmit={creation.submit}
    />
  );
}
