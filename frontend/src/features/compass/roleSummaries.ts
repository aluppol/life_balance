export function activeGoalSummary(count: number): string {
  if (count === 0) {
    return 'No active goals';
  }
  return count === 1 ? '1 active goal' : `${String(count)} active goals`;
}
