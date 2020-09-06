package httphandlers.rest.page

import ServerManager.ServerManager
import com.sun.net.httpserver.HttpExchange
import exceptions.ProcessingException
import groovy.transform.CompileStatic
import httphandlers.rest.RestHandler
import httphandlers.util.Responder
import httphandlers.util.UriParametersParser

@CompileStatic
class DeletePageHandler extends RestHandler {
    ServerManager serverManager

    @Override
    protected void respond(HttpExchange exchange, Responder responder) throws IOException {
        UriParametersParser uriParametersParser = new UriParametersParser(exchange.getRequestURI().toString())
        String url = uriParametersParser.getStringParameter('url')

        if (url == null) {
            throw new ProcessingException("Url is null.")
        }

        serverManager.deletePage(url)

        responder.sendResponse()
    }
}
