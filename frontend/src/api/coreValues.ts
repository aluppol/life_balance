import { rankedCollection } from './rankedCollection';

export interface CoreValue {
  readonly id: string;
  readonly name: string;
  readonly description: string;
  readonly position: number;
}

export const coreValueCollection = rankedCollection<CoreValue>('/api/values');
