import { useId } from 'react';
import { describedBy } from './fieldDescriptions';
import { FieldFrame } from './FieldFrame';

interface DateFieldProps {
  readonly label: string;
  readonly value: string;
  readonly error?: string | undefined;
  readonly onChange: (value: string) => void;
}

export function DateField({ label, value, error, onChange }: DateFieldProps) {
  const inputId = useId();
  return (
    <FieldFrame inputId={inputId} label={label} error={error}>
      <input
        id={inputId}
        type="date"
        value={value}
        aria-invalid={error !== undefined}
        aria-describedby={describedBy(inputId, undefined, error)}
        onChange={(event) => onChange(event.target.value)}
      />
    </FieldFrame>
  );
}
