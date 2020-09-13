package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic
import httphandlers.util.HandlerUtils
import pages.GenericPage
import server.ServerManager

import java.nio.charset.StandardCharsets

@CompileStatic
class GenericPageHandler implements HttpHandler {
    ServerManager serverManager

    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()
            uri = HandlerUtils.decodeUrl(uri)
            uri = HandlerUtils.trimLeadingSlashes(uri)

            if (serverManager.pathExists(uri)) {
                byte[] page = new GenericPage(serverManager.getParagraphs(uri)).getPage().getBytes(StandardCharsets.UTF_8)
                exchange.sendResponseHeaders(200, page.length)
                oStream.write(page)
            } else {
                exchange.sendResponseHeaders(404, 0)
            }
        } catch (IOException e) {
            println "CommonHttpHandler: ${e.getMessage()}"
//            e.printStackTrace()
        }
    }
}
