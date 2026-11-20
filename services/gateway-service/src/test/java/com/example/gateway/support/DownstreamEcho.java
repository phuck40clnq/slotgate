package com.example.gateway.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

// Stands in for a downstream service, so a test reads exactly what the gateway forwarded.
public final class DownstreamEcho {

    private final HttpServer server;

    private DownstreamEcho(HttpServer server) {
        this.server = Objects.requireNonNull(server, "server");
    }

    /** Starts a server on a free port that answers every request with the values of one request header. */
    public static DownstreamEcho start(String headerName) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/", exchange -> echo(exchange, headerName));
            server.start();
            return new DownstreamEcho(server);
        } catch (IOException exception) {
            throw new UncheckedIOException("Downstream echo did not start", exception);
        }
    }

    public String url() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    public void stop() {
        server.stop(0);
    }

    // An absent header gives an empty body, so the caller sees no body at all.
    private static void echo(HttpExchange exchange, String headerName) throws IOException {
        List<String> values = exchange.getRequestHeaders().getOrDefault(headerName, List.of());
        byte[] body = String.join(",", values).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, body.length == 0 ? -1 : body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}
