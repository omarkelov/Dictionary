import ServerManager.ServerManager;
import com.sun.net.httpserver.HttpServer;
import httphandlers.CommonHttpHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerManager serverManager = new ServerManager();

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(80), 0);

        server.createContext("/", new CommonHttpHandler(serverManager));

//        server.createContext("/login", new CommonHttpHandler(mainManager, "login"));
//        server.createContext("/api/method/sign.login", new LogInHandler(mainManager));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }
}
