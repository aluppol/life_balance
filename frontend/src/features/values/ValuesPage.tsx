import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { Section } from '../../shared/ui/Section';
import { NewEntry } from '../ranking/NewEntry';
import { RankedEntries } from '../ranking/RankedEntries';
import { useAddCoreValue, useCoreValueCommands } from './coreValueCommands';
import { useCoreValues } from './coreValueQueries';

export function ValuesPage() {
  return (
    <Page
      title="Core values"
      intro="Rank the principles you live by. When two of them pull in different directions, the higher one wins."
    >
      <Section title="Your values, in rank order">
        <CoreValueRanking />
      </Section>
      <Section title="Add a value">
        <NewEntry
          label="New value"
          submitLabel="Add value"
          confirmation="Value added."
          addition={useAddCoreValue()}
        />
      </Section>
    </Page>
  );
}

function CoreValueRanking() {
  const coreValues = useCoreValues();
  const commands = useCoreValueCommands();
  return (
    <Loadable queries={[coreValues]}>
      {(entries) => (
        <RankedEntries
          label="Core values in rank order"
          emptyMessage="No values yet. Add the first one below."
          entries={entries}
          commands={commands}
        />
      )}
    </Loadable>
  );
}
