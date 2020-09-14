package server

import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic
import httphandlers.ParagraphsPageHandler
import server.beans.ParagraphBean
import server.beans.PathBean

@CompileStatic
class ServerManager {
    private HttpServer server
    private SQLiteDatabaseHandler dbHandler
    private TemplateManager templateManager

    ServerManager(HttpServer server, SQLiteDatabaseHandler dbHandler, TemplateManager templateManager) {
        this.server = server
        this.dbHandler = dbHandler
        this.templateManager = templateManager
    }

    Collection<String> getPaths() {
        dbHandler.getPaths()
    }

    boolean pathExists(String url) {
        dbHandler.exists(url)
    }

    void createPage(PathBean pathBean) {
        pathBean.uuid = UUID.randomUUID()
        dbHandler.insertPath(pathBean)
        server.createContext("/$pathBean.path", new ParagraphsPageHandler(serverManager: this))
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

    TemplateManager getTemplateManager() {
        templateManager
    }
}
