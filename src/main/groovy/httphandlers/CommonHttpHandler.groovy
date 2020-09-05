package httphandlers

import ServerManager.ServerManager
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
class CommonHttpHandler implements HttpHandler {
    private static final String RESOURCES_FOLDER = "src/main/resources/"
    private static final String INDEX_HTML_PAGE = "index.html"
    private static final String NOT_FOUND_HTML_PAGE = "not-found.html"

    private final ServerManager serverManager

    CommonHttpHandler(ServerManager serverManager) {
        this.serverManager = serverManager
    }

    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()

            try {
                String fileName = RESOURCES_FOLDER + uri
                Path path = Paths.get(fileName)
                if (Files.isRegularFile(path)) {
                    byte[] bytes = Files.readAllBytes(path)
                    exchange.sendResponseHeaders(200, bytes.length)
                    oStream.write(bytes)

                    return
                }
            } catch (InvalidPathException ignored) {}

            int responseCode
            String pageName
            if (uri == "/") {
                responseCode = 200
                pageName = INDEX_HTML_PAGE
            } else {
                responseCode = 404
                pageName = NOT_FOUND_HTML_PAGE
            }

            byte[] page = Files.readAllBytes(Paths.get(RESOURCES_FOLDER + pageName))

            exchange.sendResponseHeaders(responseCode, page.length)
            oStream.write(page)
        } catch (IOException e) {
            println "CommonHttpHandler: ${e.getMessage()}"
//            e.printStackTrace()
        }
    }
}
