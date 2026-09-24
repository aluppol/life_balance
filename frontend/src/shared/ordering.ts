export function moveEarlier(ids: readonly string[], id: string): string[] {
  return swapWithNeighbour(ids, ids.indexOf(id), -1);
}

export function moveLater(ids: readonly string[], id: string): string[] {
  return swapWithNeighbour(ids, ids.indexOf(id), 1);
}

function swapWithNeighbour(ids: readonly string[], index: number, step: number): string[] {
  const moving = ids[index];
  const neighbour = ids[index + step];
  if (moving === undefined || neighbour === undefined) {
    return [...ids];
  }
  return ids.with(index, neighbour).with(index + step, moving);
}
