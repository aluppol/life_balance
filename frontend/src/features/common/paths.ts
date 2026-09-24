export function weekPath(monday: string): string {
  return `/week/${monday}`;
}

export function dayPath(day: string): string {
  return `/day/${day}`;
}

export function reviewPath(monday: string): string {
  return `/review/${monday}`;
}
