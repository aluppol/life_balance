import { Link } from 'react-router';
import type { CoreValue } from '../../api/coreValues';
import { Loadable } from '../../shared/ui/Loadable';
import { Section } from '../../shared/ui/Section';
import { useCoreValues } from '../values/coreValueQueries';
import styles from './Compass.module.css';

export function ValueSummary() {
  const coreValues = useCoreValues();
  return (
    <Section title="Values">
      <Loadable queries={[coreValues]}>{(values) => <RankedValueNames values={values} />}</Loadable>
    </Section>
  );
}

function RankedValueNames({ values }: { readonly values: readonly CoreValue[] }) {
  if (values.length === 0) {
    return (
      <p className={styles.invitation}>
        No values yet. <Link to="/values">Name your values</Link>
      </p>
    );
  }
  return (
    <ol aria-label="Values in rank order" className={styles.values}>
      {values.map((value) => (
        <li key={value.id}>{value.name}</li>
      ))}
    </ol>
  );
}
