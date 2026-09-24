import type { LifeRole } from '../../api/lifeRoles';
import type { WeekScorecard } from '../../api/scorecard';
import { ProgressMeter } from '../../shared/ui/ProgressMeter';
import styles from './Review.module.css';
import { quadrantRows, roleRows } from './scorecardRows';
import { TallyTable } from './TallyTable';

interface ScorecardProps {
  readonly scorecard: WeekScorecard;
  readonly roles: readonly LifeRole[];
}

export function Scorecard({ scorecard, roles }: ScorecardProps) {
  if (scorecard.overall.planned === 0) {
    return <p className={styles.empty}>Nothing was planned for this week.</p>;
  }
  return (
    <div className={styles.scorecard}>
      <ProgressMeter label="All activities" {...scorecard.overall} />
      <ProgressMeter label="Big rocks" {...scorecard.bigRocks} />
      <div className={styles.tables}>
        <TallyTable caption="By quadrant" heading="Quadrant" rows={quadrantRows(scorecard)} />
        <TallyTable caption="By role" heading="Role" rows={roleRows(scorecard, roles)} />
      </div>
    </div>
  );
}
