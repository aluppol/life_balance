import { useId } from 'react';
import type { Choice } from './choice';
import styles from './Fields.module.css';

interface RadioGroupProps<Value extends string> {
  readonly legend: string;
  readonly choices: readonly Choice<Value>[];
  readonly selected: Value;
  readonly onChange: (selected: Value) => void;
}

export function RadioGroup<Value extends string>({
  legend,
  choices,
  selected,
  onChange,
}: RadioGroupProps<Value>) {
  const groupName = useId();
  return (
    <fieldset className={styles.radioGroup}>
      <legend className={styles.legend}>{legend}</legend>
      {choices.map((choice) => (
        <label key={choice.value} className={styles.radio}>
          <input
            type="radio"
            name={groupName}
            value={choice.value}
            checked={choice.value === selected}
            onChange={() => onChange(choice.value)}
          />
          {choice.label}
        </label>
      ))}
    </fieldset>
  );
}
