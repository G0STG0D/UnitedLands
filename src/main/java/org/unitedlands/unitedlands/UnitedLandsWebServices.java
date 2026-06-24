package org.unitedlands.unitedlands;

import java.io.File;
import java.util.ArrayList;

import org.unitedlands.classes.ConfigFile;
import org.unitedlands.unitedlands.classes.webservices.ApiRouter;
import org.unitedlands.unitedlands.classes.webservices.JwtAuthHandler;
import org.unitedlands.unitedlands.classes.webservices.handlers.AuthFinalizeHandler;
import org.unitedlands.unitedlands.classes.webservices.handlers.AuthStartHandler;
import org.unitedlands.unitedlands.classes.webservices.handlers.AuthStatusHandler;
import org.unitedlands.unitedlands.classes.webservices.handlers.CitizenGetHandler;
import org.unitedlands.unitedlands.classes.webservices.handlers.PlayersHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Logger;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.HttpString;

public class UnitedLandsWebServices {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    private ConfigFile config;
    private Undertow server;

    public UnitedLandsWebServices(UnitedLands plugin) {
        this.plugin = plugin;
        config = new ConfigFile(plugin, "webservices.yml");

        cleanChallenges();
        startWebServices();
    }

    private void cleanChallenges() {
        var challengesFuture = GlobalDataManager.instance().getDatabaseManager().getLoginChallengeService()
                .getAllAsync();
        try {
            var challenges = new ArrayList<>(challengesFuture.get());
            for (var challenge : challenges) {
                if (System.currentTimeMillis() > challenge.getExpiresAt())
                    GlobalDataManager.instance().getDatabaseManager().getLoginChallengeService().deleteAsync(challenge);
            }

        } catch (Exception ex) {
            Logger.logError("Error cleaning login challenges: " + ex.getMessage());
        }

    }

    public void startWebServices() {

        var host = config.get().getString("host");
        var port = config.get().getInt("port", 8090);

        File webDir = new File(plugin.getDataFolder(), "web");
        if (!webDir.exists()) {
            webDir.mkdirs();
            plugin.saveResource("web/index.html", false);
        }

        try {
            server = buildServer(host, port, webDir);
            server.start();
            Logger.log("Web server started on " + host + ":" + port, "UnitedLands");
        } catch (Exception e) {
            Logger.logError("Failed to start web server: " + e.getMessage(), "UnitedLands");
        }
    }

    private Undertow buildServer(String host, int port, File webDir) {

        var secret = config.get().getString("signing-key");
        if (secret == null)
            throw new IllegalStateException("No signing key found, cannot start web services.");

        HttpHandler apiRouter = new ApiRouter()
                .get("/auth/status/{id}", new AuthStatusHandler())
                .get("/auth/start", new AuthStartHandler())
                .post("/auth/finalize", new AuthFinalizeHandler(secret))
                .get("/citizens/{id}", new CitizenGetHandler(plugin));

        HttpHandler resourceHandler = new ResourceHandler(
                new FileResourceManager(webDir, 1024))
                .setWelcomeFiles("index.html")
                .setDirectoryListingEnabled(false);

        HttpHandler root = Handlers.path()
                .addPrefixPath("/api", apiRouter)
                .addPrefixPath("/", resourceHandler);

        HttpHandler rootWithCors = addCorsHeaders(root);

        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(rootWithCors)
                .build();
    }

    private HttpHandler addCorsHeaders(HttpHandler next) {
        return exchange -> {
            exchange.getResponseHeaders()
                    .put(HttpString.tryFromString("Access-Control-Allow-Origin"), "*");
            exchange.getResponseHeaders()
                    .put(HttpString.tryFromString("Access-Control-Allow-Methods"),
                            "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders()
                    .put(HttpString.tryFromString("Access-Control-Allow-Headers"),
                            "Authorization, Content-Type");
            exchange.getResponseHeaders()
                    .put(HttpString.tryFromString("Access-Control-Max-Age"), "3600");

            // Handle preflight OPTIONS requests — browsers send these
            // before cross-origin POST/PUT requests with custom headers
            if (exchange.getRequestMethod().equalToString("OPTIONS")) {
                exchange.setStatusCode(200);
                exchange.endExchange();
                return;
            }

            next.handleRequest(exchange);
        };
    }

    public void stopWebServices() {
        server.stop();
    }

}
