import { http, type HttpHandler, HttpResponse, type PathParams } from 'msw';
import type { NamedDetails } from '../../api/rankedCollection';
import { created, invalidFields, noContent, problem } from './responses';
import { fieldErrors, hasErrors, isSameName, lengthError, requiredTextError } from './validation';

interface RankedRecord {
  readonly id: string;
  readonly name: string;
  readonly description: string;
  readonly position: number;
}

export interface RankedStore<Entry extends RankedRecord> {
  readonly path: string;
  readonly noun: string;
  readonly entries: () => Entry[];
  readonly replaceEntries: (entries: Entry[]) => void;
  readonly newEntry: (id: string, details: NamedDetails) => Entry;
  readonly removalConflict: (entry: Entry) => string | undefined;
  readonly forget: (entry: Entry) => void;
}

export function rankedHandlers<Entry extends RankedRecord>(
  store: RankedStore<Entry>,
): HttpHandler[] {
  return [
    http.get(store.path, () => HttpResponse.json(store.entries())),
    http.post<PathParams, NamedDetails>(store.path, async ({ request }) =>
      addEntry(store, await request.json()),
    ),
    http.put<PathParams, { ids: string[] }>(`${store.path}/order`, async ({ request }) =>
      reorderEntries(store, (await request.json()).ids),
    ),
    http.put<{ id: string }, NamedDetails>(`${store.path}/:id`, async ({ params, request }) =>
      reviseEntry(store, params.id, await request.json()),
    ),
    http.delete<{ id: string }>(`${store.path}/:id`, ({ params }) => removeEntry(store, params.id)),
  ];
}

function addEntry<Entry extends RankedRecord>(
  store: RankedStore<Entry>,
  details: NamedDetails,
): Response {
  const rejection = rejectionOf(store, details, '');
  if (rejection !== undefined) {
    return rejection;
  }
  const entry = store.newEntry(crypto.randomUUID(), details);
  store.replaceEntries(renumbered([...store.entries(), entry]));
  return created(entry, `${store.path}/${entry.id}`);
}

function reviseEntry<Entry extends RankedRecord>(
  store: RankedStore<Entry>,
  id: string,
  details: NamedDetails,
): Response {
  const entry = store.entries().find((candidate) => candidate.id === id);
  if (entry === undefined) {
    return problem(404, `No ${store.noun} ${id}`);
  }
  return (
    rejectionOf(store, details, id) ??
    replaceEntry(store, { ...entry, name: details.name, description: details.description })
  );
}

function replaceEntry<Entry extends RankedRecord>(
  store: RankedStore<Entry>,
  revised: Entry,
): Response {
  store.replaceEntries(store.entries().map((entry) => (entry.id === revised.id ? revised : entry)));
  return HttpResponse.json(revised);
}

function removeEntry<Entry extends RankedRecord>(store: RankedStore<Entry>, id: string): Response {
  const entry = store.entries().find((candidate) => candidate.id === id);
  if (entry === undefined) {
    return problem(404, `No ${store.noun} ${id}`);
  }
  const conflict = store.removalConflict(entry);
  if (conflict !== undefined) {
    return problem(409, conflict);
  }
  store.replaceEntries(renumbered(store.entries().filter((candidate) => candidate.id !== id)));
  store.forget(entry);
  return noContent();
}

function reorderEntries<Entry extends RankedRecord>(
  store: RankedStore<Entry>,
  ids: readonly string[],
): Response {
  const entries = store.entries();
  const reordered = ids.flatMap((id) => entries.filter((entry) => entry.id === id));
  if (reordered.length !== entries.length || new Set(ids).size !== ids.length) {
    return problem(422, 'The new order must list every item exactly once');
  }
  store.replaceEntries(renumbered(reordered));
  return noContent();
}

function rejectionOf<Entry extends RankedRecord>(
  store: RankedStore<Entry>,
  details: NamedDetails,
  ownId: string,
): Response | undefined {
  const errors = fieldErrors({
    name: requiredTextError(details.name, 100),
    description: lengthError(details.description, 1000),
  });
  if (hasErrors(errors)) {
    return invalidFields(errors);
  }
  const isDuplicate = store
    .entries()
    .some((entry) => entry.id !== ownId && isSameName(entry.name, details.name));
  return isDuplicate
    ? problem(409, `A ${store.noun} named '${details.name.trim()}' already exists`)
    : undefined;
}

function renumbered<Entry extends RankedRecord>(entries: readonly Entry[]): Entry[] {
  return entries.map((entry, position) => ({ ...entry, position }));
}
