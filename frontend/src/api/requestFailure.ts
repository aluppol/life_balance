import type { ProblemDetails } from './problemDetails';

export class RequestFailure extends Error {
  readonly problem: ProblemDetails;

  constructor(problem: ProblemDetails) {
    super(problem.detail);
    this.name = 'RequestFailure';
    this.problem = problem;
  }
}

export function fieldErrorsOf(failure: Error | null): Readonly<Record<string, string>> {
  return failure instanceof RequestFailure ? failure.problem.fieldErrors : {};
}
