import { expect, test, vi } from 'vitest';
import {
  addDays,
  formatCalendarDate,
  formatLongDate,
  formatShortDay,
  formatWeekdayAndDate,
  formatWeekday,
  isIsoDate,
  mondayOf,
  nextWeek,
  previousWeek,
  todayIsoDate,
  weekDays,
} from './calendar';

test.each([
  [new Date(2026, 8, 27, 23, 59), '2026-09-27'],
  [new Date(2026, 0, 1, 0, 5), '2026-01-01'],
  [new Date(2026, 10, 1, 1, 30), '2026-11-01'],
])('answers today on %s as the local calendar date %s', (now, today) => {
  vi.setSystemTime(now);
  expect(todayIsoDate()).toBe(today);
});

test('pads years, months and days to ISO widths', () => {
  expect(addDays('0987-03-03', 1)).toBe('0987-03-04');
});

test.each([
  ['2026-09-21', '2026-09-21'],
  ['2026-09-23', '2026-09-21'],
  ['2026-09-27', '2026-09-21'],
  ['2026-11-01', '2026-10-26'],
  ['2026-03-08', '2026-03-02'],
  ['2027-01-03', '2026-12-28'],
])('the week of %s starts on Monday %s', (day, monday) => {
  expect(mondayOf(day)).toBe(monday);
});

test('lists the days of a week from Monday to Sunday', () => {
  expect(weekDays('2026-09-28')).toEqual([
    '2026-09-28',
    '2026-09-29',
    '2026-09-30',
    '2026-10-01',
    '2026-10-02',
    '2026-10-03',
    '2026-10-04',
  ]);
});

test('steps whole weeks across daylight saving changes and years', () => {
  expect(nextWeek('2026-10-26')).toBe('2026-11-02');
  expect(previousWeek('2026-11-02')).toBe('2026-10-26');
  expect(nextWeek('2026-03-02')).toBe('2026-03-09');
  expect(previousWeek('2026-01-05')).toBe('2025-12-29');
});

test('adds days across months and leap days', () => {
  expect(addDays('2028-02-28', 1)).toBe('2028-02-29');
  expect(addDays('2026-02-28', 1)).toBe('2026-03-01');
  expect(addDays('2026-10-01', -1)).toBe('2026-09-30');
});

test.each([
  ['2026-09-23', true],
  ['2028-02-29', true],
  ['2026-02-29', false],
  ['2026-13-01', false],
  ['2026-9-1', false],
  ['2026-09-23T00:00', false],
  [' 2026-09-23', false],
  ['someday', false],
  ['', false],
])('%s is an ISO date: %s', (text, isValid) => {
  expect(isIsoDate(text)).toBe(isValid);
});

test('formats dates for people in US English', () => {
  expect(formatWeekday('2026-09-23')).toBe('Wednesday');
  expect(formatShortDay('2026-09-23')).toBe('Wed, Sep 23');
  expect(formatWeekdayAndDate('2026-09-23')).toBe('Wednesday, Sep 23');
  expect(formatLongDate('2026-09-23')).toBe('Wednesday, September 23, 2026');
  expect(formatCalendarDate('2026-09-23')).toBe('September 23, 2026');
});
