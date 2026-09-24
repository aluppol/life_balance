export function requiredTextError(value: unknown, maximumLength: number): string | undefined {
  if (typeof value !== 'string' || value.trim() === '') {
    return 'must not be blank';
  }
  return lengthError(value, maximumLength);
}

export function lengthError(value: unknown, maximumLength: number): string | undefined {
  return typeof value === 'string' && value.length > maximumLength
    ? `size must be between 0 and ${String(maximumLength)}`
    : undefined;
}

export function fieldErrors(
  candidates: Readonly<Record<string, string | undefined>>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(candidates).filter(
      (candidate): candidate is [string, string] => candidate[1] !== undefined,
    ),
  );
}

export function hasErrors(errors: Readonly<Record<string, string>>): boolean {
  return Object.keys(errors).length > 0;
}

export function isSameName(first: string, second: string): boolean {
  return first.trim().toLowerCase() === second.trim().toLowerCase();
}
