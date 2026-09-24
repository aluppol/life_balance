import { getJson } from './httpClient';

export interface SignedInPerson {
  readonly displayName: string;
  readonly isGuest: boolean;
}

export function fetchSignedInPerson(): Promise<SignedInPerson> {
  return getJson<SignedInPerson>('/api/me');
}
