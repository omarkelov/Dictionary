package httphandlers.rest

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import exceptions.ProcessingException
import groovy.transform.CompileStatic
import httphandlers.util.Responder

@CompileStatic
abstract class RestHandler implements HttpHandler {
    @Override
    void handle(HttpExchange exchange) {
        try (OutputStream oStream = exchange.getResponseBody()) {
            Responder responder = new Responder(exchange, oStream)

            try {
                respond(exchange, responder)
            } catch (ProcessingException e) {
                responder.sendError(e.getMessage())
            }
        } catch (IOException|RuntimeException e) {
            e.printStackTrace()
        } finally {
            exchange.close()
        }
    }

    protected abstract void respond(HttpExchange exchange, Responder responder) throws IOException
}
