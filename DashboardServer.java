import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DashboardServer {

    private static HttpServer server;
    // We keep a reference in case you want to use it later
    private static UserStore userStoreRef;

    public static void start(UserStore userStore) throws IOException {
        // already running
        if (server != null) {
            return;
        }

        userStoreRef = userStore;

        int port = 8000;
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve the main dashboard page from ui/index.html
        server.createContext("/", new StaticFileHandler("ui/index.html"));

        // You could add more endpoints here later, e.g. /api/user, etc.

        server.setExecutor(null); // default executor
        server.start();
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    // ===== Simple handler to serve a single static file =====
    private static class StaticFileHandler implements HttpHandler {
        private final String filePath;

        StaticFileHandler(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            Path path = Paths.get(filePath);
            byte[] bytes;
            int statusCode;

            if (Files.exists(path)) {
                bytes = Files.readAllBytes(path);
                statusCode = 200;
            } else {
                String notFound = "<h1>404 - index.html not found</h1>"
                        + "<p>Expected file at: " + filePath + "</p>";
                bytes = notFound.getBytes();
                statusCode = 404;
            }

            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
