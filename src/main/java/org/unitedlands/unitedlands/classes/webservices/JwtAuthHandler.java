package org.unitedlands.unitedlands.classes.webservices;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.unitedlands.unitedlands.utils.ClassLoaderUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import io.undertow.server.HttpServerExchange;

public class JwtAuthHandler implements HttpHandler {

    private final HttpHandler next;
    private final SecretKey signingKey;

    public JwtAuthHandler(HttpHandler next, String base64Secret) {
        this.next = next;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String authHeader = exchange.getRequestHeaders()
                .getFirst(Headers.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(exchange, 401, "Missing or malformed Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = ClassLoaderUtil.withPluginClassLoader(getClass(), () -> Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody());

            exchange.putAttachment(AttachmentKeys.CLAIMS, claims);
            exchange.putAttachment(AttachmentKeys.SUBJECT, claims.getSubject());

            next.handleRequest(exchange);

        } catch (ExpiredJwtException e) {
            sendError(exchange, 401, "Token expired");
        } catch (JwtException e) {
            sendError(exchange, 401, "Invalid token");
        }
    }

    private void sendError(HttpServerExchange exchange, int status, String message) {
        String json = "{\"error\":\"" + message + "\"}";
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; charset=utf-8");
        exchange.getResponseSender().send(json, StandardCharsets.UTF_8);
    }
}