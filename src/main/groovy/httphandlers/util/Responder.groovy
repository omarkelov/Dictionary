package httphandlers.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import groovy.transform.CompileStatic

@CompileStatic
class Responder {
    private static final String RESPONSE = 'response'
    private static final String ERROR = 'error'
    private static final int RESPONSE_CODE = 200
    private static final int ERROR_CODE = 400

    private HttpExchange exchange
    private OutputStream oStream

    Responder(HttpExchange exchange, OutputStream oStream) {
        this.exchange = exchange
        this.oStream = oStream
    }

    void sendResponse() throws IOException {
        send(RESPONSE_CODE, '')
    }

    void sendResponse(String message) throws IOException {
        JsonObject jsonObject = new JsonObject()
        jsonObject.addProperty(RESPONSE, message)

        send(RESPONSE_CODE, jsonObject.getAsString())
    }

    void sendResponse(JsonObject innerJsonObject) throws IOException {
        JsonObject jsonObject = new JsonObject()
        jsonObject.add(RESPONSE, innerJsonObject)

        send(RESPONSE_CODE, jsonObject.getAsString())
    }

    void sendResponse(JsonArray jsonArray) throws IOException {
        JsonObject jsonObject = new JsonObject()
        jsonObject.add(RESPONSE, jsonArray)

        send(RESPONSE_CODE, jsonObject.getAsString())
    }

    void sendError(String message) throws IOException {
        JsonObject jsonObject = new JsonObject()
        jsonObject.addProperty(ERROR, message)

        send(ERROR_CODE, jsonObject.getAsString())
    }

    private void send(int rCode, String message) throws IOException {
        byte[] bytes = message.getBytes()
        exchange.sendResponseHeaders(rCode, bytes.length)
        oStream.write(bytes)
    }
}
