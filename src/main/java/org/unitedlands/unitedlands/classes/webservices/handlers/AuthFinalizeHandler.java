package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.LoginChallenge;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.ClassLoaderUtil;
import org.unitedlands.utils.Logger;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

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
            challenge = GlobalDataManager.instance().getDatabaseManager().getLoginChallengeService().getAsync(id).get()
                    .orElse(null);
        } catch (Exception ex) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        if (challenge == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        Logger.log(challenge.getStatus(), "UnitedLands");

        if (!challenge.getStatus().equals("completed")) {
            ApiResponse.send(exchange, 403, null);
            return;
        }

        String jwtToken = issueToken(challenge.getMcUsername(), challenge.getMcUUID());

        ApiResponse.send(exchange, 200, jwtToken);
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
