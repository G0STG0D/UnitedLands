package org.unitedlands.unitedlands.classes.webservices;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class ApiResponse {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void send(HttpServerExchange exchange, int status, Object payload) {
        String json = GSON.toJson(payload);
        //byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; charset=utf-8");
        exchange.getResponseSender().send(json);
    }

    public static void error(HttpServerExchange exchange, int status, String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", message);
        send(exchange, status, body);
    }

    public static <T> T readBody(HttpServerExchange exchange, Class<T> type) throws IOException {
        // Undertow requires you to block before reading the request body
        if (exchange.isInIoThread()) exchange.startBlocking();
        try (InputStreamReader reader = new InputStreamReader(
                exchange.getInputStream(), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, type);
        }
    }
}