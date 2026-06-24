package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.Random;
import java.util.UUID;

import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.LoginChallenge;
import org.unitedlands.unitedlands.managers.GlobalDataManager;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class AuthStartHandler implements HttpHandler {

    public AuthStartHandler() {

    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        var challenge = new LoginChallenge();
        challenge.setUuid(UUID.randomUUID());
        challenge.setCode(generateCode());
        challenge.setStatus("pending");
        challenge.setCreatedAt(System.currentTimeMillis());
        challenge.setExpiresAt(System.currentTimeMillis() + 600000);

        GlobalDataManager.instance().getDatabaseManager().getLoginChallengeService().createAsync(challenge);

        ApiResponse.send(exchange, 200, challenge);
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(11);
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                sb.append('-');
            }

            char c = (char) ('A' + rnd.nextInt(26));
            sb.append(c);
        }

        return sb.toString();
    }

}
