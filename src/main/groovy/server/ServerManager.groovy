package server

import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.GenericPageHandler
import server.beans.PathBean
import server.beans.ParagraphBean

@CompileStatic
class ServerManager {
    private HttpServer server
    private SQLiteDatabaseHandler dbHandler

    ServerManager(HttpServer server) {
        this.server = server
        dbHandler = new SQLiteDatabaseHandler()
    }

    Collection<String> getPages() {
        dbHandler.getPaths()
    }

    boolean pathExists(String url) {
        dbHandler.exists(url)
    }

    void createPage(PathBean pathBean) {
        pathBean.uuid = UUID.randomUUID()
        dbHandler.insertPath(pathBean)
        server.createContext("/$pathBean.path", new GenericPageHandler(serverManager: this))
    }

    void deletePage(PathBean pathBean) { // TODO remove parents
        dbHandler.deletePath(pathBean.path)
        server.removeContext("/$pathBean.path")
    }

    void addParagraph(ParagraphBean paragraph) {
        paragraph.uuid = UUID.randomUUID()
        paragraph.phrases.each {
            it.uuid = UUID.randomUUID()
        }

        dbHandler.insertParagraph(paragraph)
    }

    Collection<ParagraphBean> getParagraphs(String path) {
        dbHandler.getParagraphs(path)
    }
}
