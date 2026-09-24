import { useState } from 'react';

export interface Editing {
  readonly editedId: string | null;
  readonly edit: (id: string) => void;
  readonly finishEditing: () => void;
}

export function useEditing(): Editing {
  const [editedId, setEditedId] = useState<string | null>(null);
  return { editedId, edit: setEditedId, finishEditing: () => setEditedId(null) };
}
