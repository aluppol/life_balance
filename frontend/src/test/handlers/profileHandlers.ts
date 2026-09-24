import { http, HttpResponse, type PathParams } from 'msw';
import type { MissionStatement } from '../../api/missionStatement';
import type { WeeklyReviewDetails } from '../../api/weeklyReview';
import { planner } from '../planner';
import { invalidFields, problem } from './responses';
import { fieldErrors, hasErrors, lengthError, requiredTextError } from './validation';

export const accountHandlers = [
  http.get('/api/me', () => HttpResponse.json(planner().signedInPerson)),
];

export const missionHandlers = [
  http.get('/api/mission', () => {
    const { mission } = planner();
    return mission === null
      ? problem(404, 'Mission statement was not found')
      : HttpResponse.json(mission);
  }),
  http.put<PathParams, MissionStatement>('/api/mission', async ({ request }) =>
    defineMission(await request.json()),
  ),
];

export const reviewHandlers = [
  http.get<{ monday: string }>('/api/weeks/:monday/review', ({ params }) =>
    findReview(params.monday),
  ),
  http.put<{ monday: string }, WeeklyReviewDetails>(
    '/api/weeks/:monday/review',
    async ({ params, request }) => recordReview(params.monday, await request.json()),
  ),
];

function defineMission(statement: MissionStatement): Response {
  const errors = fieldErrors({ text: requiredTextError(statement.text, 4000) });
  if (hasErrors(errors)) {
    return invalidFields(errors);
  }
  planner().mission = { text: statement.text };
  return HttpResponse.json(statement);
}

function findReview(monday: string): Response {
  const review = planner().reviews.find((candidate) => candidate.weekStart === monday);
  return review === undefined
    ? problem(404, `Weekly review of the week starting ${monday} was not found`)
    : HttpResponse.json(review);
}

function recordReview(monday: string, details: WeeklyReviewDetails): Response {
  const errors = fieldErrors({
    accomplishments: lengthError(details.accomplishments, 4000),
    lessons: lengthError(details.lessons, 4000),
  });
  if (hasErrors(errors)) {
    return invalidFields(errors);
  }
  const review = { weekStart: monday, ...details };
  planner().reviews = [
    ...planner().reviews.filter((candidate) => candidate.weekStart !== monday),
    review,
  ];
  return HttpResponse.json(review);
}
