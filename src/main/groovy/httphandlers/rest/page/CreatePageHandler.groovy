package httphandlers.rest.page

import com.sun.net.httpserver.HttpExchange
import exceptions.ProcessingException
import groovy.transform.CompileStatic
import httphandlers.rest.RestHandler
import httphandlers.util.Responder
import httphandlers.util.UriParametersParser
import server.ServerManager

@CompileStatic
class CreatePageHandler extends RestHandler {
    ServerManager serverManager

    @Override
    protected void respond(HttpExchange exchange, Responder responder) throws IOException {
        UriParametersParser uriParametersParser = new UriParametersParser(exchange.getRequestURI().toString())
        String url = uriParametersParser.getStringParameter('url')

        if (url == null) {
            throw new ProcessingException("Url is null.")
        }

        serverManager.createPage(url)

        responder.sendResponse()
    }
}
