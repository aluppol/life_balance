import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { Loadable } from '../../shared/ui/Loadable';
import { Section } from '../../shared/ui/Section';
import { activeGoalCount } from '../goals/goalBoard';
import { useGoals } from '../goals/goalQueries';
import { useLifeRoles } from '../roles/lifeRoleQueries';
import styles from './Compass.module.css';
import { activeGoalSummary } from './roleSummaries';

export function RoleSummary() {
  const roles = useLifeRoles();
  const goals = useGoals();
  return (
    <Section title="Roles">
      <Loadable queries={[roles, goals]}>
        {(lifeRoles, allGoals) => <RoleGoalCounts roles={lifeRoles} goals={allGoals} />}
      </Loadable>
    </Section>
  );
}

function RoleGoalCounts({
  roles,
  goals,
}: {
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
}) {
  return (
    <ul aria-label="Roles and their active goals" className={styles.roles}>
      {roles.map((role) => (
        <li key={role.id} className={styles.role}>
          <span className={styles.roleName}>{role.name}</span>
          <span className={styles.goalCount}>
            {activeGoalSummary(activeGoalCount(goals, role.id))}
          </span>
        </li>
      ))}
    </ul>
  );
}
