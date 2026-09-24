import type { Activity } from '../api/activities';
import type { CoreValue } from '../api/coreValues';
import type { Goal } from '../api/goals';
import type { LifeRole } from '../api/lifeRoles';
import type { MissionStatement } from '../api/missionStatement';
import type { SignedInPerson } from '../api/signedInPerson';
import type { WeeklyReview } from '../api/weeklyReview';
import { seededActivities, seededGoals, seededReviews, seededRoles, seededValues } from './seed';

export interface PlannerState {
  signedInPerson: SignedInPerson;
  mission: MissionStatement | null;
  values: CoreValue[];
  roles: LifeRole[];
  goals: Goal[];
  activities: Activity[];
  reviews: WeeklyReview[];
}

let state = seededPlanner();

export function planner(): PlannerState {
  return state;
}

export function resetPlanner(): void {
  state = seededPlanner();
}

function seededPlanner(): PlannerState {
  return {
    signedInPerson: { displayName: 'Ada Lovelace', isGuest: false },
    mission: { text: 'I live by principles I choose, not by moods I happen to have.' },
    values: [...seededValues],
    roles: [...seededRoles],
    goals: [...seededGoals],
    activities: [...seededActivities],
    reviews: [...seededReviews],
  };
}
