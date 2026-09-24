import { setupServer } from 'msw/node';
import { contentNegotiationHandlers } from './handlers/contentNegotiation';
import { goalHandlers } from './handlers/goalHandlers';
import { planningHandlers } from './handlers/planningHandlers';
import { accountHandlers, missionHandlers, reviewHandlers } from './handlers/profileHandlers';
import { roleHandlers, valueHandlers } from './handlers/valueAndRoleHandlers';

export const server = setupServer(
  ...contentNegotiationHandlers,
  ...accountHandlers,
  ...missionHandlers,
  ...valueHandlers,
  ...roleHandlers,
  ...goalHandlers,
  ...planningHandlers,
  ...reviewHandlers,
);
