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
        return dbHandler.getPaths()
    }

    boolean pathExists(String url) {
        dbHandler.exists(validatePath(url))
    }

    void createPage(String url) {
//        println "Create: $url"
        dbHandler.insertPath(validatePath(url))
    }

    void deletePage(String url) { // TODO remove parents
//        println "Delete: $url"
        dbHandler.deletePath(validatePath(url))
    }

    private String validatePath(String url) {
        URLDecoder.decode(url, StandardCharsets.UTF_8).replaceAll('^/+', '')
    }
}
