package httphandlers.rest

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import exceptions.ProcessingException
import groovy.transform.CompileStatic
import httphandlers.util.HandlerUtils
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
                println(e.getMessage())
                responder.sendError(e.getMessage())
            }
        } catch (IOException|RuntimeException e) {
            e.printStackTrace()
        } finally {
            exchange.close()
        }
    }

    protected String getJson(HttpExchange exchange) {
        String[] uriParts = exchange.getRequestURI().toString().split('/')
        HandlerUtils.decodeUrl(uriParts[uriParts.length - 1])
    }

    protected abstract void respond(HttpExchange exchange, Responder responder) throws IOException
}
