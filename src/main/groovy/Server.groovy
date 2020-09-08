import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.GenericPageHandler
import httphandlers.MainPageHandler
import httphandlers.ResourceHandler
import httphandlers.rest.page.CreatePageHandler
import httphandlers.rest.page.DeletePageHandler
import server.ServerManager

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors

@CompileStatic
class Server {
    public static final String SITE_NAME = 'localhost'
    public static final String RESOURCES_DIRECTORY = "src/main/resources/"
    public static final String DATABASE_DIRECTORY = "${RESOURCES_DIRECTORY}database"

    static void main(String[] args) {
        ServerManager serverManager = new ServerManager()

        HttpServer server = HttpServer.create().tap {
            bind(new InetSocketAddress(80), 0)
            setExecutor(Executors.newCachedThreadPool())

            createContext("/", new MainPageHandler(serverManager: serverManager))

            createContext("/api/page.create", new CreatePageHandler(serverManager: serverManager))
            createContext("/api/page.delete", new DeletePageHandler(serverManager: serverManager))
        }

        Files.walk(Paths.get(RESOURCES_DIRECTORY))
            .filter(path -> !path.startsWith(DATABASE_DIRECTORY) && Files.isRegularFile(path))
            .forEach(path -> {
                String pathStr = '/' + path.toString()
                    .replaceAll('\\\\', '/')
                    .replaceFirst(RESOURCES_DIRECTORY, '')

                server.createContext(pathStr, new ResourceHandler())
            })

        serverManager.getPages().each {
            server.createContext("/$it", new GenericPageHandler(serverManager: serverManager))
        }

        server.start()
    }
}
