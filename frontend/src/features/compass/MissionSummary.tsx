import { Link } from 'react-router';
import type { MissionStatement } from '../../api/missionStatement';
import { Loadable } from '../../shared/ui/Loadable';
import { Section } from '../../shared/ui/Section';
import { useMissionStatement } from '../mission/missionQueries';
import styles from './Compass.module.css';

export function MissionSummary() {
  const mission = useMissionStatement();
  return (
    <Section title="Mission">
      <Loadable queries={[mission]}>
        {(statement) => <MissionText statement={statement} />}
      </Loadable>
    </Section>
  );
}

function MissionText({ statement }: { readonly statement: MissionStatement | null }) {
  if (statement === null) {
    return (
      <p className={styles.invitation}>
        You have not written your mission yet. <Link to="/mission">Write your mission</Link>
      </p>
    );
  }
  return (
    <>
      <blockquote className={styles.mission}>{statement.text}</blockquote>
      <Link to="/mission">Edit your mission</Link>
    </>
  );
}
