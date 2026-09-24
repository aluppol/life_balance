import { delay, http } from 'msw';
import { server } from './server';

type Method = 'get' | 'post' | 'put' | 'delete';

export function delayNext(method: Method, path: string): void {
  server.use(
    http[method](
      path,
      async () => {
        await delay(200);
      },
      { once: true },
    ),
  );
}
