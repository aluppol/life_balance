import { http, HttpResponse, type PathParams } from 'msw';
import type { Goal, GoalDetails, GoalStatus } from '../../api/goals';
import { planner } from '../planner';
import { created, invalidFields, noContent, problem } from './responses';
import { fieldErrors, hasErrors, lengthError, requiredTextError } from './validation';

export const goalHandlers = [
  http.get('/api/goals', () => HttpResponse.json(planner().goals)),
  http.post<PathParams, GoalDetails>('/api/goals', async ({ request }) =>
    setGoal(await request.json()),
  ),
  http.put<{ id: string }, { status: GoalStatus }>(
    '/api/goals/:id/status',
    async ({ params, request }) => changeStatus(params.id, (await request.json()).status),
  ),
  http.put<{ id: string }, GoalDetails>('/api/goals/:id', async ({ params, request }) =>
    reviseGoal(params.id, await request.json()),
  ),
  http.delete<{ id: string }>('/api/goals/:id', ({ params }) => removeGoal(params.id)),
];

function setGoal(details: GoalDetails): Response {
  const rejection = goalRejection(details);
  if (rejection !== undefined) {
    return rejection;
  }
  const goal: Goal = { id: crypto.randomUUID(), ...details, status: 'ACTIVE' };
  planner().goals = [...planner().goals, goal];
  return created(goal, `/api/goals/${goal.id}`);
}

function reviseGoal(id: string, details: GoalDetails): Response {
  const goal = planner().goals.find((candidate) => candidate.id === id);
  if (goal === undefined) {
    return problem(404, `Goal ${id} was not found`);
  }
  return goalRejection(details) ?? replaceGoal({ ...goal, ...details });
}

function changeStatus(id: string, status: GoalStatus): Response {
  const goal = planner().goals.find((candidate) => candidate.id === id);
  if (goal === undefined) {
    return problem(404, `Goal ${id} was not found`);
  }
  const refusal = transitionRefusal(goal.status, status);
  return refusal === undefined ? replaceGoal({ ...goal, status }) : problem(422, refusal);
}

function removeGoal(id: string): Response {
  planner().goals = planner().goals.filter((goal) => goal.id !== id);
  planner().activities = planner().activities.map((activity) =>
    activity.goalId === id ? { ...activity, goalId: null } : activity,
  );
  return noContent();
}

function replaceGoal(revised: Goal): Response {
  planner().goals = planner().goals.map((goal) => (goal.id === revised.id ? revised : goal));
  return HttpResponse.json(revised);
}

function transitionRefusal(current: GoalStatus, next: GoalStatus): string | undefined {
  if (next === 'ACTIVE') {
    return current === 'ACTIVE' ? 'Only an achieved or dropped goal can be reopened' : undefined;
  }
  return current === 'ACTIVE' ? undefined : 'Only an active goal can be achieved or dropped';
}

function goalRejection(details: GoalDetails): Response | undefined {
  const errors = fieldErrors({
    title: requiredTextError(details.title, 200),
    description: lengthError(details.description, 2000),
  });
  if (hasErrors(errors)) {
    return invalidFields(errors);
  }
  const { roles, values } = planner();
  if (!roles.some((role) => role.id === details.roleId)) {
    return problem(422, `Life role ${details.roleId} does not exist`);
  }
  const unknown = details.valueIds.filter((id) => !values.some((value) => value.id === id));
  return unknown.length === 0
    ? undefined
    : problem(422, `Core values ${unknown.join(', ')} do not exist`);
}
