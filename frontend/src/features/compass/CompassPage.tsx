import { Link } from 'react-router';
import { mondayOf, previousWeek } from '../../shared/calendar';
import { Page } from '../../shared/ui/Page';
import { useToday } from '../../shared/useToday';
import { reviewPath, weekPath } from '../common/paths';
import styles from './Compass.module.css';
import { MissionSummary } from './MissionSummary';
import { RoleSummary } from './RoleSummary';
import { ValueSummary } from './ValueSummary';
import { WeekProgress } from './WeekProgress';

export function CompassPage() {
  const thisMonday = mondayOf(useToday());
  return (
    <Page
      title="Compass"
      intro="Your mission, values and roles set the direction. The week is where you walk it."
    >
      <nav aria-label="Next steps" className={styles.nextSteps}>
        <Link to={weekPath(thisMonday)} className={styles.step}>
          Plan this week
        </Link>
        <Link to={reviewPath(previousWeek(thisMonday))} className={styles.step}>
          Review last week
        </Link>
      </nav>
      <div className={styles.overview}>
        <MissionSummary />
        <WeekProgress monday={thisMonday} />
        <ValueSummary />
        <RoleSummary />
      </div>
    </Page>
  );
}
