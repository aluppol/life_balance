import { onTestFinished } from 'vitest';
import { server } from './server';

export function countLoads(path: string): () => number {
  let loads = 0;
  const countLoad = ({ request }: { request: Request }) => {
    if (request.method === 'GET' && new URL(request.url).pathname === path) {
      loads += 1;
    }
  };
  server.events.on('request:start', countLoad);
  onTestFinished(() => {
    server.events.removeListener('request:start', countLoad);
  });
  return () => loads;
}
