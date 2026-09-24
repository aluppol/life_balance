import { generateKeyPairSync, randomUUID, sign } from 'node:crypto';
import { createServer } from 'node:http';

const port = Number(process.env.DEV_IDENTITY_PORT ?? '8090');
const issuer = `http://localhost:${String(port)}/realms/dev`;
const certificatesPath = '/realms/dev/protocol/openid-connect/certs';
const tokenPath = '/token';
const tokenLifetimeSeconds = 15 * 60;
const signingKeys = createSigningKeys();

createServer(respond).listen(port, () => {
  process.stdout.write(`Dev identity server for ${issuer} on http://localhost:${String(port)}\n`);
});

function respond(request, response) {
  if (request.method === 'GET' && request.url === certificatesPath) {
    sendJson(response, 200, { keys: [signingKeys.publicJwk] });
    return;
  }
  if (request.method === 'GET' && request.url === tokenPath) {
    sendJson(response, 200, issueToken(Math.floor(Date.now() / 1000)));
    return;
  }
  sendJson(response, 404, { error: 'not_found' });
}

function createSigningKeys() {
  const { publicKey, privateKey } = generateKeyPairSync('rsa', { modulusLength: 2048 });
  const keyId = randomUUID();
  const publicJwk = {
    ...publicKey.export({ format: 'jwk' }),
    kid: keyId,
    use: 'sig',
    alg: 'RS256',
  };
  return { keyId, privateKey, publicJwk };
}

function issueToken(issuedAt) {
  const expiresAt = issuedAt + tokenLifetimeSeconds;
  return { accessToken: signedToken(claimsOf(issuedAt, expiresAt)), expiresAt };
}

function claimsOf(issuedAt, expiresAt) {
  const name = process.env.DEV_NAME ?? 'Local Developer';
  return {
    iss: issuer,
    sub: process.env.DEV_SUBJECT ?? 'local-developer',
    aud: ['lifebalance'],
    preferred_username: name,
    name,
    realm_access: { roles: realmRoles() },
    iat: issuedAt,
    exp: expiresAt,
    jti: randomUUID(),
  };
}

function realmRoles() {
  return (process.env.DEV_ROLES ?? 'USER')
    .split(',')
    .map((role) => role.trim())
    .filter((role) => role !== '');
}

function signedToken(claims) {
  const header = { alg: 'RS256', typ: 'JWT', kid: signingKeys.keyId };
  const signingInput = `${encoded(header)}.${encoded(claims)}`;
  const signature = sign('sha256', Buffer.from(signingInput), signingKeys.privateKey);
  return `${signingInput}.${signature.toString('base64url')}`;
}

function encoded(part) {
  return Buffer.from(JSON.stringify(part)).toString('base64url');
}

function sendJson(response, status, body) {
  response.writeHead(status, { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' });
  response.end(JSON.stringify(body));
}
