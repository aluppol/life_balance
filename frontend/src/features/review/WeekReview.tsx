import { formatCalendarDate } from '../../shared/calendar';
import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { Section } from '../../shared/ui/Section';
import { reviewPath } from '../common/paths';
import { WeekNavigation } from '../common/WeekNavigation';
import { useLifeRoles } from '../roles/lifeRoleQueries';
import { useWeekScorecard } from '../week/weekQueries';
import { ReviewEditor } from './ReviewEditor';
import { useWeeklyReview } from './reviewQueries';
import { Scorecard } from './Scorecard';

interface WeekReviewProps {
  readonly monday: string;
  readonly lastMonday: string;
}

export function WeekReview({ monday, lastMonday }: WeekReviewProps) {
  const scorecard = useWeekScorecard(monday);
  const roles = useLifeRoles();
  const review = useWeeklyReview(monday);
  return (
    <Page
      title={`Review of the week of ${formatCalendarDate(monday)}`}
      intro="Look back before you plan ahead: what got done, what you learned, and how you renewed yourself."
    >
      <WeekNavigation
        monday={monday}
        anchorMonday={lastMonday}
        anchorLabel="Last week"
        pathOf={reviewPath}
      />
      <Section title="Scorecard">
        <Loadable queries={[scorecard, roles]}>
          {(weekScorecard, lifeRoles) => <Scorecard scorecard={weekScorecard} roles={lifeRoles} />}
        </Loadable>
      </Section>
      <Section title="Your review">
        <Loadable queries={[review]}>
          {(weeklyReview) => <ReviewEditor key={monday} monday={monday} review={weeklyReview} />}
        </Loadable>
      </Section>
    </Page>
  );
}
