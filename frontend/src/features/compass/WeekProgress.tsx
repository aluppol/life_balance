import { Loadable } from '../../shared/ui/Loadable';
import { ProgressMeter } from '../../shared/ui/ProgressMeter';
import { Section } from '../../shared/ui/Section';
import { useWeekScorecard } from '../week/weekQueries';
import styles from './Compass.module.css';

interface WeekProgressProps {
  readonly monday: string;
}

export function WeekProgress({ monday }: WeekProgressProps) {
  const scorecard = useWeekScorecard(monday);
  return (
    <Section title="This week">
      <Loadable queries={[scorecard]}>
        {(weekScorecard) => (
          <div className={styles.progress}>
            <ProgressMeter label="Big rocks" {...weekScorecard.bigRocks} />
            <ProgressMeter label="All activities" {...weekScorecard.overall} />
          </div>
        )}
      </Loadable>
    </Section>
  );
}
