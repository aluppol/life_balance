import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { Section } from '../../shared/ui/Section';
import { type GoalContext, GoalEntry } from './GoalEntry';
import styles from './Goals.module.css';

interface RoleGoalsProps {
  readonly role: LifeRole;
  readonly goals: readonly Goal[];
  readonly context: GoalContext;
}

export function RoleGoals({ role, goals, context }: RoleGoalsProps) {
  return (
    <Section title={role.name}>
      {goals.length === 0 ? (
        <p className={styles.empty}>No goals to show for this role.</p>
      ) : (
        <ul aria-label={`Goals of ${role.name}`} className={styles.list}>
          {goals.map((goal) => (
            <li key={goal.id} className={styles.goal}>
              <GoalEntry goal={goal} context={context} />
            </li>
          ))}
        </ul>
      )}
    </Section>
  );
}
