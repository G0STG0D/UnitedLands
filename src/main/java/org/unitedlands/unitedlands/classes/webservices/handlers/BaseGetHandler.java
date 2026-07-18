package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.Map;
import org.unitedlands.unitedlands.classes.webservices.AttachmentKeys;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public abstract class BaseGetHandler implements HttpHandler {

    public BaseGetHandler() {
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
    }

    @SuppressWarnings("unchecked")
    protected String getPathParam(HttpServerExchange exchange, String name) {
        Map<String, String> params = exchange.getAttachment(AttachmentKeys.PATH_PARAMS);
        return params != null ? params.get(name) : null;
    }

}
