export function including<Choice>(
  selected: readonly Choice[],
  choice: Choice,
  allChoicesInOrder: readonly Choice[],
): Choice[] {
  return allChoicesInOrder.filter(
    (candidate) => candidate === choice || selected.includes(candidate),
  );
}

export function excluding<Choice>(selected: readonly Choice[], choice: Choice): Choice[] {
  return selected.filter((candidate) => candidate !== choice);
}
