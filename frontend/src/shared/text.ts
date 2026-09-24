export function nullWhenEmpty(text: string): string | null {
  return text === '' ? null : text;
}
