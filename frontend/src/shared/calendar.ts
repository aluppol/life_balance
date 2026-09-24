const locale = 'en-US';
const daysPerWeek = 7;

export function todayIsoDate(): string {
  return toIsoDate(new Date());
}

export function isIsoDate(text: string): boolean {
  return toIsoDate(localDateOf(text)) === text;
}

export function addDays(isoDate: string, days: number): string {
  const date = localDateOf(isoDate);
  return toIsoDate(new Date(date.getFullYear(), date.getMonth(), date.getDate() + days));
}

export function mondayOf(isoDate: string): string {
  const daysSinceMonday = (localDateOf(isoDate).getDay() + daysPerWeek - 1) % daysPerWeek;
  return addDays(isoDate, -daysSinceMonday);
}

export function weekDays(monday: string): string[] {
  return Array.from({ length: daysPerWeek }, (_, offset) => addDays(monday, offset));
}

export function nextWeek(monday: string): string {
  return addDays(monday, daysPerWeek);
}

export function previousWeek(monday: string): string {
  return addDays(monday, -daysPerWeek);
}

export function formatWeekday(isoDate: string): string {
  return localDateOf(isoDate).toLocaleDateString(locale, { weekday: 'long' });
}

export function formatShortDay(isoDate: string): string {
  return localDateOf(isoDate).toLocaleDateString(locale, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });
}

export function formatWeekdayAndDate(isoDate: string): string {
  return localDateOf(isoDate).toLocaleDateString(locale, {
    weekday: 'long',
    month: 'short',
    day: 'numeric',
  });
}

export function formatLongDate(isoDate: string): string {
  return localDateOf(isoDate).toLocaleDateString(locale, {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  });
}

export function formatCalendarDate(isoDate: string): string {
  return localDateOf(isoDate).toLocaleDateString(locale, {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  });
}

function toIsoDate(date: Date): string {
  const year = String(date.getFullYear()).padStart(4, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function localDateOf(isoDate: string): Date {
  return new Date(`${isoDate}T00:00:00`);
}
