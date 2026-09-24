import { useState } from 'react';
import type { MissionStatement } from '../../api/missionStatement';
import { feedbackOf } from '../common/mutationFeedback';
import { useDefineMissionStatement } from './missionCommands';

export function useMissionDraft(statement: MissionStatement | null) {
  const definition = useDefineMissionStatement();
  const [text, setText] = useState(statement?.text ?? '');
  return {
    text,
    changeText: setText,
    submit: () => definition.mutate({ text }),
    feedback: feedbackOf(definition, 'Mission saved.'),
  };
}
