import { rankedCollection } from './rankedCollection';

type LifeRoleKind = 'PERSONAL' | 'SHARPEN_THE_SAW';

export interface LifeRole {
  readonly id: string;
  readonly name: string;
  readonly description: string;
  readonly kind: LifeRoleKind;
  readonly position: number;
}

export const lifeRoleCollection = rankedCollection<LifeRole>('/api/roles');
