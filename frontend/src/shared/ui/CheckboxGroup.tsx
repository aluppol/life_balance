import { useId } from 'react';
import { excluding, including } from '../choices';
import type { Choice } from './choice';
import styles from './Fields.module.css';

interface CheckboxGroupProps<Value extends string> {
  readonly legend: string;
  readonly choices: readonly Choice<Value>[];
  readonly selected: readonly Value[];
  readonly onChange: (selected: Value[]) => void;
}

export function CheckboxGroup<Value extends string>({
  legend,
  choices,
  selected,
  onChange,
}: CheckboxGroupProps<Value>) {
  const allValues = choices.map((choice) => choice.value);
  return (
    <fieldset className={styles.checkboxGroup}>
      <legend className={styles.legend}>{legend}</legend>
      {choices.map((choice) => (
        <Checkbox
          key={choice.value}
          choice={choice}
          isChecked={selected.includes(choice.value)}
          onCheck={() => onChange(including(selected, choice.value, allValues))}
          onUncheck={() => onChange(excluding(selected, choice.value))}
        />
      ))}
    </fieldset>
  );
}

interface CheckboxProps<Value extends string> {
  readonly choice: Choice<Value>;
  readonly isChecked: boolean;
  readonly onCheck: () => void;
  readonly onUncheck: () => void;
}

function Checkbox<Value extends string>({
  choice,
  isChecked,
  onCheck,
  onUncheck,
}: CheckboxProps<Value>) {
  const inputId = useId();
  const hintId = `${inputId}-hint`;
  return (
    <div className={styles.choice}>
      <input
        id={inputId}
        type="checkbox"
        checked={isChecked}
        aria-describedby={choice.description === undefined ? undefined : hintId}
        onChange={isChecked ? onUncheck : onCheck}
      />
      <label htmlFor={inputId}>{choice.label}</label>
      {choice.description === undefined ? null : (
        <p id={hintId} className={styles.choiceHint}>
          {choice.description}
        </p>
      )}
    </div>
  );
}
