import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.CommonHttpHandler
import httphandlers.GenericPageHandler
import httphandlers.rest.page.CreatePageHandler
import httphandlers.rest.page.DeletePageHandler
import server.ServerManager

import java.util.concurrent.Executors

@CompileStatic
class Server {
    public static final String SITE_NAME = 'localhost'

    static void main(String[] args) {
        ServerManager serverManager = new ServerManager()

        HttpServer server = HttpServer.create().tap {
            bind(new InetSocketAddress(80), 0)
            setExecutor(Executors.newCachedThreadPool())

            createContext("/", new CommonHttpHandler(serverManager: serverManager))

            createContext("/api/page.create", new CreatePageHandler(serverManager: serverManager))
            createContext("/api/page.delete", new DeletePageHandler(serverManager: serverManager))
        }

        serverManager.getPages().each {
            server.createContext("/$it", new GenericPageHandler(serverManager: serverManager))
        }

        server.start()
    }
}
