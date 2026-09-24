import type { RenewalDimension } from '../../api/weeklyReview';
import type { Choice } from '../../shared/ui/choice';

export const renewalChoices: readonly Choice<RenewalDimension>[] = [
  { value: 'PHYSICAL', label: 'Physical', description: 'Exercise, nutrition, rest.' },
  { value: 'MENTAL', label: 'Mental', description: 'Reading, learning, planning.' },
  {
    value: 'SOCIAL_EMOTIONAL',
    label: 'Social/Emotional',
    description: 'Service, empathy, connection.',
  },
  { value: 'SPIRITUAL', label: 'Spiritual', description: 'Values, meditation, nature.' },
];
