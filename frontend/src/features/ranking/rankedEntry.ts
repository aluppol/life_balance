import type { UseMutationResult } from '@tanstack/react-query';
import type { NamedDetails } from '../../api/rankedCollection';
import type { Revision } from '../../api/revision';

export interface RankedEntry {
  readonly id: string;
  readonly name: string;
  readonly description: string;
}

export interface RankedEntryCommands {
  readonly revision: UseMutationResult<void, Error, Revision<NamedDetails>>;
  readonly removal: UseMutationResult<void, Error, string>;
  readonly reorder: UseMutationResult<void, Error, readonly string[]>;
}

export const emptyNamedDetails: NamedDetails = { name: '', description: '' };

export function detailsOf(entry: RankedEntry): NamedDetails {
  return { name: entry.name, description: entry.description };
}
