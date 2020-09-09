package server

import groovy.transform.CompileStatic

import java.nio.charset.StandardCharsets

@CompileStatic
class ServerManager {
    private SQLiteDatabaseHandler dbHandler

    ServerManager() {
        dbHandler = new SQLiteDatabaseHandler()
    }

    Collection<String> getPages() {
        dbHandler.getPaths()
    }

    boolean pathExists(String url) {
        dbHandler.exists(url)
    }

    void createPage(String url) {
//        println "Create: $url"
        dbHandler.insertPath(url)
    }

    void deletePage(String url) { // TODO remove parents
//        println "Delete: $url"
        dbHandler.deletePath(url)
    }
}
