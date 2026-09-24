import type { UseMutationResult } from '@tanstack/react-query';
import { fieldErrorsOf } from '../../api/requestFailure';
import type { Feedback } from '../../shared/ui/feedback';

interface MutationOutcome {
  readonly error: Error | null;
}

interface Resettable {
  readonly reset: () => void;
}

export function feedbackOf<Variables>(
  mutation: UseMutationResult<void, Error, Variables>,
  confirmation: string,
): Feedback {
  return {
    ...unconfirmedFeedbackOf(mutation),
    confirmation: mutation.isSuccess ? confirmation : '',
  };
}

export function unconfirmedFeedbackOf<Variables>(
  mutation: UseMutationResult<void, Error, Variables>,
): Feedback {
  return {
    isSaving: mutation.isPending,
    failure: mutation.error?.message ?? '',
    fieldErrors: fieldErrorsOf(mutation.error),
    confirmation: '',
  };
}

export function failureOf(mutations: readonly MutationOutcome[]): string {
  return mutations.map((mutation) => mutation.error).find((error) => error !== null)?.message ?? '';
}

export function resetEach(mutations: readonly Resettable[]): void {
  for (const mutation of mutations) {
    mutation.reset();
  }
}
