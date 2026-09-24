package com.luppol.lifebalance;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TestAccessTokens {
    public static final String ISSUER = "https://auth.example.test/realms/luppol";
    public static final String AUDIENCE = "lifebalance";

    private static final RSAKey SIGNING_KEY = generateKey();
    private static final HttpServer JWKS_SERVER = startJwksServer();

    private TestAccessTokens() {
    }

    public static RSAKey signingKey() {
        return SIGNING_KEY;
    }

    public static String jwkSetUri() {
        return "http://localhost:%d/certs".formatted(JWKS_SERVER.getAddress().getPort());
    }

    public static String token(String subject, List<String> roles) {
        return sign(claims(subject, roles).build(), SIGNING_KEY);
    }

    public static JWTClaimsSet.Builder claims(String subject, List<String> roles) {
        return new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject(subject)
                .audience(List.of(AUDIENCE, "account"))
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(300)))
                .claim("realm_access", Map.of("roles", roles))
                .claim("preferred_username", subject);
    }

    public static String sign(JWTClaimsSet claims, RSAKey key) {
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims);
            jwt.sign(new RSASSASigner(key));
            return jwt.serialize();
        } catch (JOSEException signingFailure) {
            throw new IllegalStateException(signingFailure);
        }
    }

    public static String signedWithAnotherKey(String subject, List<String> roles) {
        RSAKey stranger = generateKey();
        return sign(claims(subject, roles).build(), new RSAKey.Builder(stranger).keyID(SIGNING_KEY.getKeyID()).build());
    }

    private static RSAKey generateKey() {
        try {
            return new RSAKeyGenerator(2048).keyID(UUID.randomUUID().toString()).generate();
        } catch (JOSEException generationFailure) {
            throw new IllegalStateException(generationFailure);
        }
    }

    private static HttpServer startJwksServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            byte[] body = new JWKSet(SIGNING_KEY.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
            server.createContext("/certs", exchange -> {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream response = exchange.getResponseBody()) {
                    response.write(body);
                }
            });
            server.start();
            return server;
        } catch (IOException startFailure) {
            throw new UncheckedIOException(startFailure);
        }
    }
}
