package org.unitedlands.unitedlands.classes.webservices;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiRouter implements HttpHandler {

    private final FormParserFactory formParserFactory = FormParserFactory.builder().build();

    // Exact routes — matched first
    private final Map<String, HttpHandler> exactRoutes = new LinkedHashMap<>();

    // Parameterised routes — matched second
    // Key is the original pattern e.g. "GET:/players/{id}"
    private final List<ParameterisedRoute> paramRoutes = new ArrayList<>();

    public ApiRouter get(String path, HttpHandler handler) {
        register("GET", path, handler);
        return this;
    }

    public ApiRouter post(String path, HttpHandler handler) {
        register("POST", path,
                new EagerFormParsingHandler(formParserFactory).setNext(handler));
        return this;
    }

    public ApiRouter put(String path, HttpHandler handler) {
        register("PUT", path,
                new EagerFormParsingHandler(formParserFactory).setNext(handler));
        return this;
    }

    public ApiRouter delete(String path, HttpHandler handler) {
        register("DELETE", path, handler);
        return this;
    }

    private void register(String method, String path, HttpHandler handler) {
        if (path.contains("{")) {
            paramRoutes.add(new ParameterisedRoute(method, path, handler));
        } else {
            exactRoutes.put(method + ":" + path, handler);
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String path = exchange.getRequestPath().replaceFirst("^/api", "");
        String method = exchange.getRequestMethod().toString();

        // 1. Try exact match first
        HttpHandler handler = exactRoutes.get(method + ":" + path);
        if (handler != null) {
            handler.handleRequest(exchange);
            return;
        }

        // 2. Try parameterised routes
        for (ParameterisedRoute route : paramRoutes) {
            Map<String, String> params = route.match(method, path);
            if (params != null) {
                exchange.putAttachment(AttachmentKeys.PATH_PARAMS, params);
                route.handler.handleRequest(exchange);
                return;
            }
        }

        // 3. Check if path exists under a different method → 405 vs 404
        boolean pathExists = exactRoutes.keySet().stream()
                .anyMatch(k -> k.endsWith(":" + path))
                || paramRoutes.stream()
                        .anyMatch(r -> r.match(null, path) != null);

        sendError(exchange, pathExists ? 405 : 404,
                pathExists ? "Method not allowed" : "Route not found");
    }

    private void sendError(HttpServerExchange exchange, int status, String message) {
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; charset=utf-8");
        exchange.getResponseSender().send("{\"error\":\"" + message + "\"}");
    }

    // --- Inner class representing a single parameterised route ---

    private static class ParameterisedRoute {

        private final String method; // null means "any method" (used for 405 check)
        private final Pattern pattern;
        private final List<String> paramNames;
        final HttpHandler handler;

        ParameterisedRoute(String method, String pathTemplate, HttpHandler handler) {
            this.method = method;
            this.handler = handler;
            this.paramNames = new ArrayList<>();

            // Convert "/players/{id}/stats/{stat}" to a regex
            // capturing each {name} segment
            StringBuilder regex = new StringBuilder("^");
            Matcher m = Pattern.compile("\\{(\\w+)\\}").matcher(pathTemplate);
            int last = 0;
            while (m.find()) {
                regex.append(Pattern.quote(pathTemplate.substring(last, m.start())));
                regex.append("([^/]+)");
                paramNames.add(m.group(1));
                last = m.end();
            }
            regex.append(Pattern.quote(pathTemplate.substring(last)));
            regex.append("$");
            this.pattern = Pattern.compile(regex.toString());
        }

        /**
         * Returns extracted params if method and path match, null otherwise.
         * Pass null for method to match any method (used for 405 detection).
         */
        Map<String, String> match(String method, String path) {
            if (method != null && !this.method.equals(method))
                return null;
            Matcher m = pattern.matcher(path);
            if (!m.matches())
                return null;

            Map<String, String> params = new LinkedHashMap<>();
            for (int i = 0; i < paramNames.size(); i++) {
                params.put(paramNames.get(i), m.group(i + 1));
            }
            return params;
        }
    }
}

// public class ApiRouter implements HttpHandler {

// private final Map<String, HttpHandler> routes = new LinkedHashMap<>();
// private final FormParserFactory formParserFactory =
// FormParserFactory.builder().build();

// public ApiRouter get(String path, HttpHandler handler) {
// routes.put("GET:" + path, handler);
// return this;
// }

// public ApiRouter post(String path, HttpHandler handler) {
// routes.put("POST:" + path, new
// EagerFormParsingHandler(formParserFactory).setNext(handler));
// return this;
// }

// public ApiRouter put(String path, HttpHandler handler) {
// routes.put("PUT:" + path, handler);
// return this;
// }

// public ApiRouter delete(String path, HttpHandler handler) {
// routes.put("DELETE:" + path, handler);
// return this;
// }

// @Override
// public void handleRequest(HttpServerExchange exchange) throws Exception {
// String path = exchange.getRequestPath().replaceFirst("^/api", "");
// String method = exchange.getRequestMethod().toString();
// String key = method + ":" + path;

// HttpHandler handler = routes.get(key);

// if (handler != null) {
// handler.handleRequest(exchange);
// } else {
// boolean pathExists = routes.keySet().stream()
// .anyMatch(k -> k.endsWith(":" + path));
// sendError(exchange, pathExists ? 405 : 404,
// pathExists ? "Method not allowed" : "Route not found");
// }
// }

// private void sendError(HttpServerExchange exchange, int status, String
// message) {
// exchange.setStatusCode(status);
// exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json;
// charset=utf-8");
// exchange.getResponseSender().send("{\"error\":\"" + message + "\"}");
// }
// }