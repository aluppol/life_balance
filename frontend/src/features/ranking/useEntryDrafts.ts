import type { UseMutationResult } from '@tanstack/react-query';
import { useState } from 'react';
import type { NamedDetails } from '../../api/rankedCollection';
import { feedbackOf, unconfirmedFeedbackOf } from '../common/mutationFeedback';
import {
  detailsOf,
  emptyNamedDetails,
  type RankedEntry,
  type RankedEntryCommands,
} from './rankedEntry';

export function useEntryCreation(
  addition: UseMutationResult<void, Error, NamedDetails>,
  confirmation: string,
) {
  const [draft, setDraft] = useState(emptyNamedDetails);
  return {
    draft,
    changeDraft: setDraft,
    submit: () => addition.mutate(draft, { onSuccess: () => setDraft(emptyNamedDetails) }),
    feedback: feedbackOf(addition, confirmation),
  };
}

export function useEntryRevision(
  entry: RankedEntry,
  revision: RankedEntryCommands['revision'],
  onRevised: () => void,
) {
  const [draft, setDraft] = useState(() => detailsOf(entry));
  return {
    draft,
    changeDraft: setDraft,
    submit: () => revision.mutate({ id: entry.id, details: draft }, { onSuccess: onRevised }),
    feedback: unconfirmedFeedbackOf(revision),
  };
}
