package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.LoginChallenge;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ClassLoaderUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.util.Headers;

public class AuthFinalizeHandler extends BasePostHandler {

    private final SecretKey signingKey;

    public AuthFinalizeHandler(String base64Secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    @Override
    protected void handleForm(HttpServerExchange exchange, FormData formData) throws Exception {
        var fields = requireFields(exchange, formData, "id");
        if (fields == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        UUID id = null;
        try {
            id = UUID.fromString(fields[0]);
        } catch (Exception ex) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        LoginChallenge challenge = null;
        try {
            challenge = UnitedLandsDataManager.instance().getDatabaseManager().getLoginChallengeService().getAsync(id).get()
                    .orElse(null);
        } catch (Exception ex) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        if (challenge == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        if (!challenge.getStatus().equals("completed")) {
            ApiResponse.send(exchange, 403, null);
            return;
        }

        String jwtToken = issueToken(challenge.getMcUsername(), challenge.getMcUUID());
        Map<String, Object> response = Map.of("success", true);

        var cookieString = "session=" + jwtToken + "; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=2147483647";

        ApiResponse.send(exchange, 200, response, Map.of(Headers.SET_COOKIE, cookieString));
    }

    public String issueToken(String username, UUID userUuid) {
        return ClassLoaderUtil.withPluginClassLoader(getClass(), () -> Jwts.builder()
                .setSubject(username)
                .claim("username", username)
                .claim("uuid", userUuid.toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofDays(7))))
                .signWith(signingKey)
                .compact());

    }

}
