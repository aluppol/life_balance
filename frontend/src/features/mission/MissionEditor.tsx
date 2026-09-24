import type { MissionStatement } from '../../api/missionStatement';
import { MissionForm } from './MissionForm';
import { useMissionDraft } from './useMissionDraft';

interface MissionEditorProps {
  readonly statement: MissionStatement | null;
}

export function MissionEditor({ statement }: MissionEditorProps) {
  const editor = useMissionDraft(statement);
  return (
    <>
      {statement === null ? (
        <p>You have not written your mission yet. A few honest sentences are enough to start.</p>
      ) : null}
      <MissionForm
        text={editor.text}
        feedback={editor.feedback}
        onChange={editor.changeText}
        onSubmit={editor.submit}
      />
    </>
  );
}
