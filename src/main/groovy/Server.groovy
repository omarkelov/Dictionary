import ServerManager.ServerManager
import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.CommonHttpHandler

import java.util.concurrent.Executors

@CompileStatic
class Server {
    static void main(String[] args) {
        ServerManager serverManager = new ServerManager()

        HttpServer.create().tap {
            bind(new InetSocketAddress(80), 0)

            createContext("/", new CommonHttpHandler(serverManager))

//            createContext("/login", new CommonHttpHandler(mainManager, "login"))
//            createContext("/api/method/sign.login", new LogInHandler(mainManager))

            setExecutor(Executors.newCachedThreadPool())
            start()
        }
    }
}
