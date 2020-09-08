package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic
import pages.MainPage
import server.ServerManager

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

import static Server.RESOURCES_DIRECTORY

@CompileStatic
class MainPageHandler implements HttpHandler {
    private static final String NOT_FOUND_HTML_PAGE = "not-found.html"

    ServerManager serverManager

    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()

            int responseCode
            byte[] page
            if (uri == "/") {
                responseCode = 200
                page = new MainPage(serverManager.getPages()).getPage().getBytes(StandardCharsets.UTF_8)
            } else {
                responseCode = 404
                page = Files.readAllBytes(Paths.get(RESOURCES_DIRECTORY + NOT_FOUND_HTML_PAGE))
            }

            exchange.sendResponseHeaders(responseCode, page.length)
            oStream.write(page)
        } catch (IOException e) {
            println "CommonHttpHandler: ${e.getMessage()}"
//            e.printStackTrace()
        }
    }
}
