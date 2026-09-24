export function hintIdOf(inputId: string): string {
  return `${inputId}-hint`;
}

export function errorIdOf(inputId: string): string {
  return `${inputId}-error`;
}

export function describedBy(
  inputId: string,
  hint: string | undefined,
  error: string | undefined,
): string | undefined {
  const ids = [
    hint === undefined ? undefined : hintIdOf(inputId),
    error === undefined ? undefined : errorIdOf(inputId),
  ].filter((id) => id !== undefined);
  return ids.length === 0 ? undefined : ids.join(' ');
}
