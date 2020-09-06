package httphandlers.util;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class Responder {

    private static final String RESPONSE = "response";
    private static final String ERROR = "error";
    private static final int RESPONSE_CODE = 200;
    private static final int ERROR_CODE = 400;

    private HttpExchange exchange;
    private OutputStream oStream;

    public Responder(HttpExchange exchange, OutputStream oStream) {
        this.exchange = exchange;
        this.oStream = oStream;
    }

    public void sendResponse() throws IOException {
        send(RESPONSE_CODE, "");
    }

    public void sendResponse(String message) throws IOException {
        send(RESPONSE_CODE, new JSONObject().put(RESPONSE, message).toString());
    }

    public void sendResponse(JSONObject jsonObject) throws IOException {
        send(RESPONSE_CODE, new JSONObject().put(RESPONSE, jsonObject).toString());
    }

    public void sendResponse(JSONArray jsonArray) throws IOException {
        send(RESPONSE_CODE, new JSONObject().put(RESPONSE, jsonArray).toString());
    }

    public void sendError(String message) throws IOException {
        send(ERROR_CODE, new JSONObject().put(ERROR, message).toString());
    }

    private void send(int rCode, String message) throws IOException {
        byte[] bytes = message.getBytes();
        exchange.sendResponseHeaders(rCode, bytes.length);
        oStream.write(bytes);
        //System.out.println("Response sent: " + new String(bytes));
    }
}
