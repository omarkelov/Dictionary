import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.MainPageHandler
import httphandlers.ParagraphsPageHandler
import httphandlers.ResourceHandler
import httphandlers.rest.page.PageCreateHandler
import httphandlers.rest.page.PageDeleteHandler
import httphandlers.rest.paragraph.ParagraphAddHandler
import server.SQLiteDatabaseHandler
import server.ServerManager
import server.TemplateManager

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors

@CompileStatic
class Server {
    public static final String SITE_NAME = 'localhost'
    public static final String RESOURCES_DIRECTORY = 'src/main/resources/'
    public static final String DATABASE_DIRECTORY = "${RESOURCES_DIRECTORY}database/"

    public static final String MAIN_PAGE_NAME = 'main'
    public static final String PARAGRAPHS_PAGE_NAME = 'paragraphs'

    static void main(String[] args) {
        HttpServer server = HttpServer.create().tap {
            bind(new InetSocketAddress(80), 0)
            setExecutor(Executors.newCachedThreadPool())
        }

        ServerManager serverManager = new ServerManager(
            server,
            new SQLiteDatabaseHandler(),
            new TemplateManager(MAIN_PAGE_NAME, PARAGRAPHS_PAGE_NAME)
        )

        server.createContext('/', new MainPageHandler(serverManager: serverManager))
        server.createContext('/api/page.create', new PageCreateHandler(serverManager: serverManager))
        server.createContext('/api/page.delete', new PageDeleteHandler(serverManager: serverManager))
        server.createContext('/api/paragraph.add', new ParagraphAddHandler(serverManager: serverManager))

        Files.walk(Paths.get(RESOURCES_DIRECTORY))
            .filter(path -> !path.startsWith(DATABASE_DIRECTORY) && Files.isRegularFile(path))
            .forEach(path -> {
                String pathStr = '/' + path.toString()
                    .replaceAll('\\\\', '/')
                    .replaceFirst(RESOURCES_DIRECTORY, '')

                server.createContext(pathStr, new ResourceHandler())
            })

        serverManager.getPaths().each {
            server.createContext("/$it", new ParagraphsPageHandler(serverManager: serverManager))
        }

        server.start()
    }
}
