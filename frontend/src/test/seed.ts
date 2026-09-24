import type { Activity } from '../api/activities';
import type { CoreValue } from '../api/coreValues';
import type { Goal } from '../api/goals';
import type { LifeRole } from '../api/lifeRoles';
import type { WeeklyReview } from '../api/weeklyReview';

export const thisMonday = '2026-09-21';
export const lastMonday = '2026-09-14';

export const seededValues: readonly CoreValue[] = [
  {
    id: 'value-integrity',
    name: 'Integrity',
    description: 'Keep promises, especially the small ones.',
    position: 0,
  },
  { id: 'value-family', name: 'Family', description: 'Be fully present at home.', position: 1 },
  {
    id: 'value-growth',
    name: 'Growth',
    description: 'Learn something hard every quarter.',
    position: 2,
  },
  { id: 'value-health', name: 'Health', description: '', position: 3 },
];

export const seededRoles: readonly LifeRole[] = [
  {
    id: 'role-parent',
    name: 'Parent',
    description: 'Raise curious, kind and brave kids.',
    kind: 'PERSONAL',
    position: 0,
  },
  {
    id: 'role-engineer',
    name: 'Engineer',
    description: 'Ship software people trust.',
    kind: 'PERSONAL',
    position: 1,
  },
  {
    id: 'role-saw',
    name: 'Sharpen the Saw',
    description: 'Renew body, mind, heart and spirit.',
    kind: 'SHARPEN_THE_SAW',
    position: 2,
  },
  { id: 'role-friend', name: 'Friend', description: '', kind: 'PERSONAL', position: 3 },
];

export const seededGoals: readonly Goal[] = [
  {
    id: 'goal-bike',
    roleId: 'role-parent',
    title: 'Teach Mia to ride a bike',
    description: 'No training wheels by her birthday.',
    dueOn: '2026-10-14',
    status: 'ACTIVE',
    valueIds: ['value-family', 'value-growth'],
  },
  {
    id: 'goal-offline',
    roleId: 'role-engineer',
    title: 'Ship offline mode',
    description: '',
    dueOn: null,
    status: 'ACTIVE',
    valueIds: ['value-integrity'],
  },
  {
    id: 'goal-run',
    roleId: 'role-saw',
    title: 'Run a 10K under 55 minutes',
    description: 'Three runs a week.',
    dueOn: '2026-11-18',
    status: 'ACTIVE',
    valueIds: ['value-health'],
  },
  {
    id: 'goal-habits',
    roleId: 'role-saw',
    title: 'Re-read The 7 Habits',
    description: '',
    dueOn: null,
    status: 'ACHIEVED',
    valueIds: ['value-growth'],
  },
];

export const seededActivities: readonly Activity[] = [
  {
    id: 'activity-planning',
    weekStart: thisMonday,
    roleId: 'role-saw',
    goalId: null,
    title: 'Weekly planning',
    quadrant: 'IMPORTANT_NOT_URGENT',
    scheduledOn: '2026-09-21',
    isCompleted: true,
  },
  {
    id: 'activity-outage',
    weekStart: thisMonday,
    roleId: 'role-engineer',
    goalId: null,
    title: 'Fix the checkout outage',
    quadrant: 'IMPORTANT_URGENT',
    scheduledOn: '2026-09-21',
    isCompleted: true,
  },
  {
    id: 'activity-tempo',
    weekStart: thisMonday,
    roleId: 'role-saw',
    goalId: 'goal-run',
    title: 'Tempo run, 5K',
    quadrant: 'IMPORTANT_NOT_URGENT',
    scheduledOn: '2026-09-23',
    isCompleted: false,
  },
  {
    id: 'activity-bike',
    weekStart: thisMonday,
    roleId: 'role-parent',
    goalId: 'goal-bike',
    title: 'Bike practice in the park',
    quadrant: 'IMPORTANT_NOT_URGENT',
    scheduledOn: '2026-09-26',
    isCompleted: false,
  },
  {
    id: 'activity-survey',
    weekStart: thisMonday,
    roleId: 'role-engineer',
    goalId: null,
    title: 'Answer the vendor survey',
    quadrant: 'NOT_IMPORTANT_URGENT',
    scheduledOn: null,
    isCompleted: false,
  },
  {
    id: 'activity-design',
    weekStart: lastMonday,
    roleId: 'role-engineer',
    goalId: 'goal-offline',
    title: 'Write the design doc',
    quadrant: 'IMPORTANT_NOT_URGENT',
    scheduledOn: '2026-09-15',
    isCompleted: true,
  },
  {
    id: 'activity-long-run',
    weekStart: lastMonday,
    roleId: 'role-saw',
    goalId: 'goal-run',
    title: 'Long run, 7K',
    quadrant: 'IMPORTANT_NOT_URGENT',
    scheduledOn: '2026-09-20',
    isCompleted: false,
  },
  {
    id: 'activity-status',
    weekStart: lastMonday,
    roleId: 'role-engineer',
    goalId: null,
    title: 'Sit in on status meetings',
    quadrant: 'NOT_IMPORTANT_URGENT',
    scheduledOn: '2026-09-17',
    isCompleted: true,
  },
];

export const seededReviews: readonly WeeklyReview[] = [
  {
    weekStart: lastMonday,
    accomplishments: 'Finished the design doc.',
    lessons: 'Status meetings ate Thursday.',
    renewedDimensions: ['PHYSICAL', 'MENTAL'],
  },
];
