import type { Quadrant } from '../../api/activities';
import type { Choice } from '../../shared/ui/choice';

export const bigRockQuadrant: Quadrant = 'IMPORTANT_NOT_URGENT';

export const quadrantOrder: readonly Quadrant[] = [
  'IMPORTANT_URGENT',
  'IMPORTANT_NOT_URGENT',
  'NOT_IMPORTANT_URGENT',
  'NOT_IMPORTANT_NOT_URGENT',
];

export const otherQuadrants: readonly Quadrant[] = quadrantOrder.filter(
  (quadrant) => quadrant !== bigRockQuadrant,
);

export const quadrantLabels: Readonly<Record<Quadrant, string>> = {
  IMPORTANT_URGENT: 'Q1 · Important and urgent',
  IMPORTANT_NOT_URGENT: 'Q2 · Important, not urgent',
  NOT_IMPORTANT_URGENT: 'Q3 · Urgent, not important',
  NOT_IMPORTANT_NOT_URGENT: 'Q4 · Neither urgent nor important',
};

export const quadrantChoices: readonly Choice<Quadrant>[] = quadrantOrder.map((quadrant) => ({
  value: quadrant,
  label: quadrantLabels[quadrant],
}));
