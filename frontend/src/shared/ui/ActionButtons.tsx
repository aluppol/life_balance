import buttons from './buttons.module.css';

interface EditButtonProps {
  readonly name: string;
  readonly onEdit: () => void;
}

interface DeleteButtonProps {
  readonly name: string;
  readonly isDisabled: boolean;
  readonly onDelete: () => void;
}

interface CancelButtonProps {
  readonly onCancel: () => void;
}

export function EditButton({ name, onEdit }: EditButtonProps) {
  return (
    <button type="button" aria-label={`Edit ${name}`} onClick={onEdit}>
      Edit
    </button>
  );
}

export function DeleteButton({ name, isDisabled, onDelete }: DeleteButtonProps) {
  return (
    <button
      type="button"
      className={buttons.danger}
      aria-label={`Delete ${name}`}
      disabled={isDisabled}
      onClick={onDelete}
    >
      Delete
    </button>
  );
}

export function CancelButton({ onCancel }: CancelButtonProps) {
  return (
    <button type="button" onClick={onCancel}>
      Cancel
    </button>
  );
}
