import type { Activity } from '../../api/activities';
import { Subsection } from '../../shared/ui/Section';
import { ActivityList } from './ActivityList';
import type { ActivityContext } from './ActivityEntry';
import { otherQuadrants, quadrantLabels } from './quadrants';
import { activitiesInQuadrant } from './activityFacts';

interface OtherQuadrantsProps {
  readonly activities: readonly Activity[];
  readonly context: ActivityContext;
}

export function OtherQuadrants({ activities, context }: OtherQuadrantsProps) {
  return otherQuadrants.map((quadrant) => (
    <Subsection key={quadrant} title={quadrantLabels[quadrant]}>
      <ActivityList
        label={quadrantLabels[quadrant]}
        activities={activitiesInQuadrant(activities, quadrant)}
        emptyMessage="Nothing planned here."
        context={context}
      />
    </Subsection>
  ));
}
