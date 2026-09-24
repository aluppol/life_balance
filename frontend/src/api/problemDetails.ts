export interface ProblemDetails {
  readonly status: number;
  readonly detail: string;
  readonly fieldErrors: Readonly<Record<string, string>>;
}

export function parseProblemDetails(status: number, body: string): ProblemDetails {
  const members = membersOf(body);
  return {
    status,
    detail: firstText([members.detail, members.title]) ?? fallbackDetail(status),
    fieldErrors: textMembers(members.errors),
  };
}

function membersOf(body: string): Readonly<Record<string, unknown>> {
  try {
    const parsed: unknown = JSON.parse(body);
    return isRecord(parsed) ? parsed : {};
  } catch {
    return {};
  }
}

function isRecord(candidate: unknown): candidate is Readonly<Record<string, unknown>> {
  return typeof candidate === 'object' && candidate !== null && !Array.isArray(candidate);
}

function firstText(candidates: readonly unknown[]): string | undefined {
  return candidates.find(
    (candidate): candidate is string => typeof candidate === 'string' && candidate.trim() !== '',
  );
}

function textMembers(candidate: unknown): Readonly<Record<string, string>> {
  if (!isRecord(candidate)) {
    return {};
  }
  return Object.fromEntries(
    Object.entries(candidate).filter(
      (member): member is [string, string] => typeof member[1] === 'string',
    ),
  );
}

function fallbackDetail(status: number): string {
  return status >= 500
    ? 'Life Balance could not complete the request. Please try again.'
    : `The request failed with status ${String(status)}.`;
}
