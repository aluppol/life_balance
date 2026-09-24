import type { Activity } from '../../api/activities';
import type { Goal } from '../../api/goals';
import type { LifeRole } from '../../api/lifeRoles';
import { FailureMessage } from '../../shared/ui/Messages';
import { Section } from '../../shared/ui/Section';
import { useEditing } from '../../shared/useEditing';
import { ActivityList } from './ActivityList';
import { NewActivity } from './NewActivity';
import { OtherQuadrants } from './OtherQuadrants';
import { bigRockQuadrant } from './quadrants';
import { useActivityActions } from './useActivityActions';
import { WeekGrid } from './WeekGrid';
import { activitiesInQuadrant } from './activityFacts';

interface WeekBoardProps {
  readonly monday: string;
  readonly activities: readonly Activity[];
  readonly roles: readonly LifeRole[];
  readonly goals: readonly Goal[];
}

export function WeekBoard({ monday, activities, roles, goals }: WeekBoardProps) {
  const context = { roles, goals, editing: useEditing(), actions: useActivityActions(monday) };
  return (
    <>
      <FailureMessage message={context.actions.failure} />
      <Section title="Big rocks first">
        <ActivityList
          label="Big rocks"
          activities={activitiesInQuadrant(activities, bigRockQuadrant)}
          emptyMessage="No big rocks planned yet. Start with one for each role."
          context={context}
        />
      </Section>
      <Section title="Everything else">
        <OtherQuadrants activities={activities} context={context} />
      </Section>
      <Section title="The week at a glance">
        <WeekGrid monday={monday} activities={activities} />
      </Section>
      <Section title="Plan an activity">
        <NewActivity monday={monday} roles={roles} goals={goals} />
      </Section>
    </>
  );
}
