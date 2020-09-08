package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic
import server.ServerManager

@CompileStatic
class GenericPageHandler implements HttpHandler {
    ServerManager serverManager

    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()

            if (serverManager.pathExists(uri)) {
                byte[] page = uri.getBytes()
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
