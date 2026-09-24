import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { Section } from '../../shared/ui/Section';
import { NewEntry } from '../ranking/NewEntry';
import { RankedEntries } from '../ranking/RankedEntries';
import { useAddLifeRole, useLifeRoleCommands } from './lifeRoleCommands';
import { builtInBadge, isRemovable } from './lifeRoleKinds';
import { useLifeRoles } from './lifeRoleQueries';

export function RolesPage() {
  return (
    <Page
      title="Roles"
      intro="Name the roles you play. Sharpen the Saw is built in (Habit 7): the time you spend renewing body, mind, heart and spirit."
    >
      <Section title="Your roles">
        <LifeRoleRanking />
      </Section>
      <Section title="Add a role">
        <NewEntry
          label="New role"
          submitLabel="Add role"
          confirmation="Role added."
          addition={useAddLifeRole()}
        />
      </Section>
    </Page>
  );
}

function LifeRoleRanking() {
  const lifeRoles = useLifeRoles();
  const commands = useLifeRoleCommands();
  return (
    <Loadable queries={[lifeRoles]}>
      {(entries) => (
        <RankedEntries
          label="Roles in order"
          emptyMessage="No roles yet."
          entries={entries}
          commands={commands}
          badgeOf={builtInBadge}
          isRemovable={isRemovable}
        />
      )}
    </Loadable>
  );
}
