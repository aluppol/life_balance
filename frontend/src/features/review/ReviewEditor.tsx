import type { WeeklyReview } from '../../api/weeklyReview';
import { ReviewForm } from './ReviewForm';
import { useReviewDraft } from './useReviewDraft';

interface ReviewEditorProps {
  readonly monday: string;
  readonly review: WeeklyReview | null;
}

export function ReviewEditor({ monday, review }: ReviewEditorProps) {
  const editor = useReviewDraft(monday, review);
  return (
    <ReviewForm
      draft={editor.draft}
      feedback={editor.feedback}
      onChange={editor.changeDraft}
      onSubmit={editor.submit}
    />
  );
}
