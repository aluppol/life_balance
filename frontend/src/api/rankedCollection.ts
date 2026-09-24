import { deleteAt, getJson, postJson, putJson } from './httpClient';
import type { Revision } from './revision';

export interface NamedDetails {
  readonly name: string;
  readonly description: string;
}

interface RankedCollection<Entry> {
  readonly fetchAll: () => Promise<Entry[]>;
  readonly add: (details: NamedDetails) => Promise<void>;
  readonly revise: (revision: Revision<NamedDetails>) => Promise<void>;
  readonly remove: (id: string) => Promise<void>;
  readonly reorder: (ids: readonly string[]) => Promise<void>;
}

export function rankedCollection<Entry>(path: string): RankedCollection<Entry> {
  return {
    fetchAll: () => getJson<Entry[]>(path),
    add: (details) => postJson(path, details),
    revise: ({ id, details }) => putJson(`${path}/${id}`, details),
    remove: (id) => deleteAt(`${path}/${id}`),
    reorder: (ids) => putJson(`${path}/order`, { ids }),
  };
}
