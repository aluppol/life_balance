import { useState } from 'react';
import type { CoreValue } from '../../api/coreValues';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { FailureMessage } from '../../shared/ui/Messages';
import { RadioGroup } from '../../shared/ui/RadioGroup';
import { Section } from '../../shared/ui/Section';
import { useEditing } from '../../shared/useEditing';
import { goalsByRole } from './goalBoard';
import { type GoalFilter, goalFilterChoices } from './goalStatuses';
import { NewGoal } from './NewGoal';
import { RoleGoals } from './RoleGoals';
import { useGoalActions } from './useGoalActions';

interface GoalWorkspaceProps {
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
  readonly coreValues: readonly CoreValue[];
}

export function GoalWorkspace({ roles, goals, coreValues }: GoalWorkspaceProps) {
  const [filter, setFilter] = useState<GoalFilter>('ACTIVE');
  const context = { roles, coreValues, editing: useEditing(), actions: useGoalActions() };
  return (
    <>
      <RadioGroup
        legend="Show goals"
        choices={goalFilterChoices}
        selected={filter}
        onChange={setFilter}
      />
      <FailureMessage message={context.actions.failure} />
      {goalsByRole(roles, goals, filter).map((roleGoals) => (
        <RoleGoals
          key={roleGoals.role.id}
          role={roleGoals.role}
          goals={roleGoals.goals}
          context={context}
        />
      ))}
      <Section title="Set a new goal">
        <NewGoal roles={roles} coreValues={coreValues} />
      </Section>
    </>
  );
}
