import { getOptionalJson, putJson } from './httpClient';

export interface MissionStatement {
  readonly text: string;
}

const missionPath = '/api/mission';

export function fetchMissionStatement(): Promise<MissionStatement | null> {
  return getOptionalJson<MissionStatement>(missionPath);
}

export function defineMissionStatement(statement: MissionStatement): Promise<void> {
  return putJson(missionPath, statement);
}
