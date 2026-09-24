export const queryKeys = {
  signedInPerson: ['me'],
  missionStatement: ['mission'],
  coreValues: ['values'],
  lifeRoles: ['roles'],
  goals: ['goals'],
  allActivities: ['activities'],
  weekActivities: (weekStart: string) => ['activities', weekStart],
  weekScorecard: (weekStart: string) => ['scorecard', weekStart],
  weeklyReview: (weekStart: string) => ['review', weekStart],
} as const;
