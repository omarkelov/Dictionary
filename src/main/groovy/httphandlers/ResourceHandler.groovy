package httphandlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

import static Server.RESOURCES_DIRECTORY

@CompileStatic
class ResourceHandler implements HttpHandler {
    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            String uri = exchange.getRequestURI().toString()

            byte[] resource
            try {
                String fileName = RESOURCES_DIRECTORY + uri
                Path path = Paths.get(fileName)
                if (Files.isRegularFile(path)) {
                    resource = Files.readAllBytes(path)
                }
            } catch (InvalidPathException ignored) {}

            if (resource) {
                exchange.sendResponseHeaders(200, resource.length)
                oStream.write(resource)
            } else {
                exchange.sendResponseHeaders(404, 0)
            }
        } catch (IOException e) {
            println "ResourceHandler: ${e.getMessage()}"
//            e.printStackTrace()
        }
    }
}
