import { isIsoDate, mondayOf } from '../../shared/calendar';

type MondayRequest =
  | { readonly kind: 'monday'; readonly monday: string }
  | { readonly kind: 'redirect'; readonly monday: string }
  | { readonly kind: 'invalid' };

type DayRequest = { readonly kind: 'day'; readonly day: string } | { readonly kind: 'invalid' };

export function resolveMonday(param: string | undefined, fallbackMonday: string): MondayRequest {
  if (param === undefined) {
    return { kind: 'monday', monday: fallbackMonday };
  }
  if (!isIsoDate(param)) {
    return { kind: 'invalid' };
  }
  const monday = mondayOf(param);
  return monday === param ? { kind: 'monday', monday } : { kind: 'redirect', monday };
}

export function resolveDay(param: string | undefined, fallbackDay: string): DayRequest {
  if (param === undefined) {
    return { kind: 'day', day: fallbackDay };
  }
  return isIsoDate(param) ? { kind: 'day', day: param } : { kind: 'invalid' };
}
