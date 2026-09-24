import type { ServerResponse } from 'node:http';
import type { Connect, Plugin } from 'vite';

interface IssuedToken {
  readonly accessToken: string;
  readonly expiresAt: number;
}

const accessTokenHeader = 'x-forwarded-access-token';
const renewalMarginSeconds = 30;

export function accessTokenForwarding(proxiedPaths: readonly string[]): Plugin {
  return {
    name: 'life-balance-access-token-forwarding',
    apply: 'serve',
    configureServer(server) {
      const currentToken = cachedAccessToken(identityTokenUrl());
      server.middlewares.use(forwardAccessToken(proxiedPaths, currentToken));
    },
  };
}

function forwardAccessToken(
  proxiedPaths: readonly string[],
  currentToken: () => Promise<string>,
): Connect.NextHandleFunction {
  return (request, response, next) => {
    if (!isProxied(request.url ?? '', proxiedPaths)) {
      next();
      return;
    }
    currentToken().then(
      (token) => {
        request.headers[accessTokenHeader] = token;
        next();
      },
      () => refuseWithoutToken(response),
    );
  };
}

function identityTokenUrl(): string {
  return `http://localhost:${process.env.DEV_IDENTITY_PORT ?? '8090'}/token`;
}

function cachedAccessToken(tokenUrl: string): () => Promise<string> {
  let cached: IssuedToken | undefined;
  return async () => {
    if (cached === undefined || isExpiring(cached)) {
      cached = await fetchToken(tokenUrl);
    }
    return cached.accessToken;
  };
}

function isExpiring(token: IssuedToken): boolean {
  return token.expiresAt - renewalMarginSeconds <= Date.now() / 1000;
}

async function fetchToken(tokenUrl: string): Promise<IssuedToken> {
  const response = await fetch(tokenUrl);
  if (!response.ok) {
    throw new Error(`The dev identity server answered ${String(response.status)}`);
  }
  return (await response.json()) as IssuedToken;
}

function isProxied(url: string, proxiedPaths: readonly string[]): boolean {
  return proxiedPaths.some((path) => url.startsWith(path));
}

function refuseWithoutToken(response: ServerResponse): void {
  response.statusCode = 502;
  response.setHeader('Content-Type', 'text/plain; charset=utf-8');
  response.end('No access token: start the local identity server with "npm run dev:identity".');
}
