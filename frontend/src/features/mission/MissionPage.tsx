import { Loadable } from '../../shared/ui/Loadable';
import { Page } from '../../shared/ui/Page';
import { MissionEditor } from './MissionEditor';
import { useMissionStatement } from './missionQueries';

export function MissionPage() {
  const mission = useMissionStatement();
  return (
    <Page
      title="Mission"
      intro="Habit 2: Begin with the end in mind. Write down who you want to be, what you want to do, and the principles behind both. Your values, roles, goals and weeks hang off it."
    >
      <Loadable queries={[mission]}>
        {(statement) => <MissionEditor statement={statement} />}
      </Loadable>
    </Page>
  );
}
