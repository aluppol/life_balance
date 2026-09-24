import { useId } from 'react';
import { describedBy } from './fieldDescriptions';
import { FieldFrame } from './FieldFrame';

interface TextAreaFieldProps {
  readonly label: string;
  readonly value: string;
  readonly maxLength: number;
  readonly rows: number;
  readonly hint?: string | undefined;
  readonly error?: string | undefined;
  readonly onChange: (value: string) => void;
}

export function TextAreaField({
  label,
  value,
  maxLength,
  rows,
  hint,
  error,
  onChange,
}: TextAreaFieldProps) {
  const inputId = useId();
  return (
    <FieldFrame inputId={inputId} label={label} hint={hint} error={error}>
      <textarea
        id={inputId}
        value={value}
        maxLength={maxLength}
        rows={rows}
        aria-invalid={error !== undefined}
        aria-describedby={describedBy(inputId, hint, error)}
        onChange={(event) => onChange(event.target.value)}
      />
    </FieldFrame>
  );
}
