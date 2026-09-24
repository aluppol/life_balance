import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { useLifeRoles } from '../roles/lifeRoleQueries';
import { useCoreValues } from '../values/coreValueQueries';
import { useGoals } from './goalQueries';
import { GoalWorkspace } from './GoalWorkspace';

export function GoalsPage() {
  const roles = useLifeRoles();
  const goals = useGoals();
  const coreValues = useCoreValues();
  return (
    <Page
      title="Goals"
      intro="Begin with the end in mind: every goal belongs to one of your roles and serves the values behind it."
    >
      <Loadable queries={[roles, goals, coreValues]}>
        {(lifeRoles, allGoals, values) => (
          <GoalWorkspace roles={lifeRoles} goals={allGoals} coreValues={values} />
        )}
      </Loadable>
    </Page>
  );
}
