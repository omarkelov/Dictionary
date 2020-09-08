package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.Paths

import static Server.RESOURCES_DIRECTORY

@CompileStatic
class ResourceHandler implements HttpHandler {
    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()

            String fileName = RESOURCES_DIRECTORY + uri
            byte[] bytes = Files.readAllBytes(Paths.get(fileName))

            exchange.sendResponseHeaders(200, bytes.length)
            oStream.write(bytes)
        } catch (IOException e) {
            println "ResourceHandler: ${e.getMessage()}"
//            e.printStackTrace()
        }
    }
}
