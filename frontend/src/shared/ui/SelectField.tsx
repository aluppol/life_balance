import { useId } from 'react';
import type { Choice } from './choice';
import { describedBy } from './fieldDescriptions';
import { FieldFrame } from './FieldFrame';

interface SelectFieldProps<Value extends string> {
  readonly label: string;
  readonly value: Value;
  readonly choices: readonly Choice<Value>[];
  readonly error?: string | undefined;
  readonly onChange: (value: Value) => void;
}

export function SelectField<Value extends string>({
  label,
  value,
  choices,
  error,
  onChange,
}: SelectFieldProps<Value>) {
  const inputId = useId();
  return (
    <FieldFrame inputId={inputId} label={label} error={error}>
      <select
        id={inputId}
        value={value}
        aria-invalid={error !== undefined}
        aria-describedby={describedBy(inputId, undefined, error)}
        onChange={(event) => selectChoice(choices, event.target.value, onChange)}
      >
        {choices.map((choice) => (
          <option key={choice.value} value={choice.value}>
            {choice.label}
          </option>
        ))}
      </select>
    </FieldFrame>
  );
}

function selectChoice<Value extends string>(
  choices: readonly Choice<Value>[],
  selected: string,
  onChange: (value: Value) => void,
): void {
  const choice = choices.find((candidate) => candidate.value === selected);
  if (choice !== undefined) {
    onChange(choice.value);
  }
}
