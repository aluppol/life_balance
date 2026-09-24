import { useState } from 'react';
import type { WeeklyReview } from '../../api/weeklyReview';
import { feedbackOf } from '../common/mutationFeedback';
import { useRecordWeeklyReview } from './reviewCommands';
import { detailsOfReview } from './reviewDrafts';

export function useReviewDraft(weekStart: string, review: WeeklyReview | null) {
  const recording = useRecordWeeklyReview(weekStart);
  const [draft, setDraft] = useState(() => detailsOfReview(review));
  return {
    draft,
    changeDraft: setDraft,
    submit: () => recording.mutate(draft),
    feedback: feedbackOf(recording, 'Review saved.'),
  };
}
