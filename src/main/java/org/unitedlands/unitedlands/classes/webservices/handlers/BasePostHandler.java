package org.unitedlands.unitedlands.classes.webservices.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.Headers;
import io.undertow.util.Methods;

public abstract class BasePostHandler implements HttpHandler {

    @Override
    public final void handleRequest(HttpServerExchange exchange) throws Exception {

        if (!exchange.getRequestMethod().equals(Methods.POST)) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.startBlocking();

        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        // if (formData == null) {
        // sendError(exchange, 400, "Could not parse form data");
        // return;
        // }

        handleForm(exchange, formData);
    }

    /**
     * Implement this in each concrete handler.
     * Runs on a worker thread; form data is guaranteed non-null.
     */
    protected abstract void handleForm(HttpServerExchange exchange, FormData formData)
            throws Exception;

    // --- Shared utilities available to all subclasses ---

    protected String getField(FormData formData, String field) {
        FormData.FormValue value = formData.getFirst(field);
        return value != null ? value.getValue() : null;
    }

    /**
     * Reads required fields. Returns null and sends a 400 if any are missing.
     */
    protected String[] requireFields(HttpServerExchange exchange,
            FormData formData,
            String... fields) {
        if (formData == null)
            return null;
        String[] values = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            values[i] = getField(formData, fields[i]);
            if (values[i] == null) {
                sendError(exchange, 400, "Missing required field: " + fields[i]);
                return null;
            }
        }
        return values;
    }

    protected void sendError(HttpServerExchange exchange, int status, String message) {
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; charset=utf-8");
        exchange.getResponseSender().send("{\"error\":\"" + message + "\"}");
    }

    protected void sendSuccess(HttpServerExchange exchange, String json) {
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; charset=utf-8");
        exchange.getResponseSender().send(json);
    }
}