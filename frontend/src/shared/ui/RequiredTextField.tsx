import { useId } from 'react';
import { describedBy } from './fieldDescriptions';
import { FieldFrame } from './FieldFrame';

interface RequiredTextFieldProps {
  readonly label: string;
  readonly value: string;
  readonly maxLength: number;
  readonly error?: string | undefined;
  readonly onChange: (value: string) => void;
}

export function RequiredTextField({
  label,
  value,
  maxLength,
  error,
  onChange,
}: RequiredTextFieldProps) {
  const inputId = useId();
  return (
    <FieldFrame inputId={inputId} label={label} error={error}>
      <input
        id={inputId}
        type="text"
        required
        value={value}
        maxLength={maxLength}
        aria-invalid={error !== undefined}
        aria-describedby={describedBy(inputId, undefined, error)}
        onChange={(event) => onChange(event.target.value)}
      />
    </FieldFrame>
  );
}
