package httphandlers.rest.paragraph

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import groovy.transform.CompileStatic
import httphandlers.rest.RestHandler
import httphandlers.util.HandlerUtils
import httphandlers.util.Responder
import server.ServerManager
import server.beans.ParagraphBean

@CompileStatic
class ParagraphAddHandler extends RestHandler {
    ServerManager serverManager

    @Override
    protected void respond(HttpExchange exchange, Responder responder) throws IOException {
        ParagraphBean phrasesBean = new Gson().fromJson(getJson(exchange), ParagraphBean)
        phrasesBean.path = HandlerUtils.trimLeadingSlashes(phrasesBean.path)

        serverManager.addParagraph(phrasesBean)

        responder.sendResponse()
    }
}
