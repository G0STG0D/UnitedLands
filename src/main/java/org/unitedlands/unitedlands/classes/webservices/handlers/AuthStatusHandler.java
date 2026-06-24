package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.UUID;

import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.LoginChallenge;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import io.undertow.server.HttpServerExchange;

public class AuthStatusHandler extends BaseGetHandler {

    public AuthStatusHandler() {
        super();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        var idParam = getPathParam(exchange, "id");
        if (idParam == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        UUID id = null;
        try {
            id = UUID.fromString(idParam);
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

        ApiResponse.send(exchange, 200, challenge);
    }
}
