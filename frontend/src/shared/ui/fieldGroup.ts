export interface FieldGroup<Draft> {
  readonly draft: Draft;
  readonly errors: Readonly<Record<string, string>>;
  readonly onChange: (patch: Partial<Draft>) => void;
}
