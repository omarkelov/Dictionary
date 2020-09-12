package httphandlers.rest.phrase

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import groovy.transform.CompileStatic
import httphandlers.rest.RestHandler
import httphandlers.util.HandlerUtils
import httphandlers.util.Responder
import server.ServerManager
import server.beans.PhrasesBean

@CompileStatic
class PhrasesAddHandler extends RestHandler {
    ServerManager serverManager

    @Override
    protected void respond(HttpExchange exchange, Responder responder) throws IOException {
        PhrasesBean phrasesBean = new Gson().fromJson(getJson(exchange), PhrasesBean)
        phrasesBean.path = HandlerUtils.trimLeadingSlashes(phrasesBean.path)

        serverManager.addPhrases(phrasesBean)

        responder.sendResponse()
    }
}
