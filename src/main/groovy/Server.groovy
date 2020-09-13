import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.GenericPageHandler
import httphandlers.MainPageHandler
import httphandlers.ResourceHandler
import httphandlers.rest.page.PageCreateHandler
import httphandlers.rest.page.PageDeleteHandler
import httphandlers.rest.paragraph.ParagraphAddHandler
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
        HttpServer server = HttpServer.create().tap {
            bind(new InetSocketAddress(80), 0)
            setExecutor(Executors.newCachedThreadPool())
        }

        ServerManager serverManager = new ServerManager(server)

        server.createContext("/", new MainPageHandler(serverManager: serverManager))
        server.createContext("/api/page.create", new PageCreateHandler(serverManager: serverManager))
        server.createContext("/api/page.delete", new PageDeleteHandler(serverManager: serverManager))
        server.createContext("/api/paragraph.add", new ParagraphAddHandler(serverManager: serverManager))

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
