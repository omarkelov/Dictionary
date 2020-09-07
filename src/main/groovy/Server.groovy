import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.CommonHttpHandler
import httphandlers.rest.page.CreatePageHandler
import httphandlers.rest.page.DeletePageHandler
import server.ServerManager

import java.util.concurrent.Executors

@CompileStatic
class Server {
    static void main(String[] args) {
        ServerManager serverManager = new ServerManager()

        HttpServer.create().tap {
            bind(new InetSocketAddress(80), 0)

            createContext("/", new CommonHttpHandler(serverManager))

            createContext("/api/page.create", new CreatePageHandler(serverManager: serverManager))
            createContext("/api/page.delete", new DeletePageHandler(serverManager: serverManager))

            setExecutor(Executors.newCachedThreadPool())
            start()
        }
    }
}
