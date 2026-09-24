export interface Feedback {
  readonly isSaving: boolean;
  readonly failure: string;
  readonly fieldErrors: Readonly<Record<string, string>>;
  readonly confirmation: string;
}
