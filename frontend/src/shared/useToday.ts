import { useState } from 'react';
import { todayIsoDate } from './calendar';

export function useToday(): string {
  const [today] = useState(todayIsoDate);
  return today;
}
