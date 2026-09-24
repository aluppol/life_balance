import { http, HttpResponse } from 'msw';
import type { Activity, ActivityDetails } from '../../api/activities';
import { isIsoDate, mondayOf, weekDays } from '../../shared/calendar';
import { planner } from '../planner';
import { created, invalidFields, noContent, problem } from './responses';
import { scorecardOf } from './scorecards';
import { fieldErrors, hasErrors, requiredTextError } from './validation';

export const planningHandlers = [
  http.get<{ monday: string }>('/api/weeks/:monday/activities', ({ params }) =>
    ofMonday(params.monday, () => HttpResponse.json(activitiesOfWeek(params.monday))),
  ),
  http.get<{ monday: string }>('/api/weeks/:monday/scorecard', ({ params }) =>
    ofMonday(params.monday, () =>
      HttpResponse.json(scorecardOf(params.monday, activitiesOfWeek(params.monday))),
    ),
  ),
  http.post<{ monday: string }, ActivityDetails>(
    '/api/weeks/:monday/activities',
    async ({ params, request }) => planActivity(params.monday, await request.json()),
  ),
  http.put<{ id: string }, ActivityDetails>('/api/activities/:id', async ({ params, request }) =>
    reviseActivity(params.id, await request.json()),
  ),
  http.put<{ id: string }>('/api/activities/:id/completion', ({ params }) =>
    setCompletion(params.id, true),
  ),
  http.delete<{ id: string }>('/api/activities/:id/completion', ({ params }) =>
    setCompletion(params.id, false),
  ),
  http.delete<{ id: string }>('/api/activities/:id', ({ params }) => removeActivity(params.id)),
];

function ofMonday(monday: string, respond: () => Response): Response {
  return isIsoDate(monday) && mondayOf(monday) === monday
    ? respond()
    : problem(400, `${monday} is not a Monday`);
}

function activitiesOfWeek(monday: string): Activity[] {
  return planner()
    .activities.filter((activity) => activity.weekStart === monday)
    .sort((first, second) => dayRank(first).localeCompare(dayRank(second)));
}

function dayRank(activity: Activity): string {
  return activity.scheduledOn ?? '9999-12-31';
}

function planActivity(monday: string, details: ActivityDetails): Response {
  const rejection = activityRejection(monday, details);
  if (rejection !== undefined) {
    return rejection;
  }
  const activity: Activity = {
    id: crypto.randomUUID(),
    weekStart: monday,
    isCompleted: false,
    ...details,
  };
  planner().activities = [...planner().activities, activity];
  return created(activity, `/api/activities/${activity.id}`);
}

function reviseActivity(id: string, details: ActivityDetails): Response {
  const activity = planner().activities.find((candidate) => candidate.id === id);
  if (activity === undefined) {
    return problem(404, `Activity ${id} was not found`);
  }
  return (
    activityRejection(activity.weekStart, details) ?? replaceActivity({ ...activity, ...details })
  );
}

function setCompletion(id: string, isCompleted: boolean): Response {
  const activity = planner().activities.find((candidate) => candidate.id === id);
  return activity === undefined
    ? problem(404, `Activity ${id} was not found`)
    : replaceActivity({ ...activity, isCompleted });
}

function removeActivity(id: string): Response {
  planner().activities = planner().activities.filter((activity) => activity.id !== id);
  return noContent();
}

function replaceActivity(revised: Activity): Response {
  planner().activities = planner().activities.map((activity) =>
    activity.id === revised.id ? revised : activity,
  );
  return HttpResponse.json(revised);
}

function activityRejection(monday: string, details: ActivityDetails): Response | undefined {
  const errors = fieldErrors({ title: requiredTextError(details.title, 200) });
  if (hasErrors(errors)) {
    return invalidFields(errors);
  }
  const violation = ruleViolation(monday, details);
  return violation === undefined ? undefined : problem(422, violation);
}

function ruleViolation(monday: string, details: ActivityDetails): string | undefined {
  const { roles, goals } = planner();
  if (!roles.some((role) => role.id === details.roleId)) {
    return `Life role ${details.roleId} does not exist`;
  }
  const goal = goals.find((candidate) => candidate.id === details.goalId);
  if (goal !== undefined && goal.roleId !== details.roleId) {
    return `Goal '${goal.title}' belongs to another role`;
  }
  const isInWeek = details.scheduledOn === null || weekDays(monday).includes(details.scheduledOn);
  return isInWeek ? undefined : `${details.scheduledOn} is outside the week starting ${monday}`;
}
