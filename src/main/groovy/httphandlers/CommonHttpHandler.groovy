package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic
import pages.MainPage
import server.ServerManager

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
class CommonHttpHandler implements HttpHandler {
    private static final String RESOURCES_FOLDER = "src/main/resources/"
    private static final String INDEX_HTML_PAGE = "index.html"
    private static final String NOT_FOUND_HTML_PAGE = "not-found.html"

    private static final List<String> PATHS = List.of(
        'Movies',
        'Series',
        'Series/Breaking Bad',
        'Series/Westworld/Season 1',
        'Series/Westworld/Season 1/01. The Original',
        'Series/Westworld/Season 1/02. Chestnut',
        'Series/Westworld/Season 1/03. The Stray',
        'Series/Westworld/Season 1/04. Dissonance Theory',
        'Series/Westworld/Season 1/05. Contrapasso',
        'Series/Westworld/Season 1/06. The Adversary',
        'Series/Westworld/Season 2',
        'Music',
        'Books'
    )

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
//                    println uri
                    oStream.write(bytes)

                    return
                }
            } catch (InvalidPathException ignored) {}

            int responseCode
            byte[] page
            if (uri == "/") {
                responseCode = 200
                page = new MainPage(PATHS).getPage().getBytes(StandardCharsets.UTF_8)
            } else {
                responseCode = 404
                page = Files.readAllBytes(Paths.get(RESOURCES_FOLDER + NOT_FOUND_HTML_PAGE))
            }

            exchange.sendResponseHeaders(responseCode, page.length)
            oStream.write(page)
        } catch (IOException e) {
            println "CommonHttpHandler: ${e.getMessage()}"
//            e.printStackTrace()
        }
    }
}
