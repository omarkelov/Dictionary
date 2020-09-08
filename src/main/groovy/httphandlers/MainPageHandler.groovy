package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic
import pages.MainPage
import server.ServerManager

import java.nio.charset.StandardCharsets

@CompileStatic
class MainPageHandler implements HttpHandler {
    ServerManager serverManager

    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()

            if (uri == "/") {
                byte[] page = new MainPage(serverManager.getPages()).getPage().getBytes(StandardCharsets.UTF_8)
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
