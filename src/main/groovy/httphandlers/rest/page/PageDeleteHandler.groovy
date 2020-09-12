package httphandlers.rest.page

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import groovy.transform.CompileStatic
import httphandlers.rest.RestHandler
import httphandlers.util.HandlerUtils
import httphandlers.util.Responder
import server.ServerManager
import server.beans.PathBean

@CompileStatic
class PageDeleteHandler extends RestHandler {
    ServerManager serverManager

    @Override
    protected void respond(HttpExchange exchange, Responder responder) throws IOException {
        PathBean pathBean = new Gson().fromJson(getJson(exchange), PathBean)
        pathBean.path = HandlerUtils.trimLeadingSlashes(pathBean.path)

        serverManager.deletePage(pathBean)

        responder.sendResponse()
    }
}
