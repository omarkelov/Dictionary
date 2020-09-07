package server

import groovy.transform.CompileStatic

@CompileStatic
class ServerManager {

    private SQLiteDatabaseHandler dbHandler

    ServerManager() {
        dbHandler = new SQLiteDatabaseHandler()
    }

    Collection<String> getPages() {
        return dbHandler.getPaths()
    }

    void createPage(String url) {
//        println "Create: $url"
        dbHandler.insertPath(url.replaceAll('^/+', ''))
    }

    void deletePage(String url) { // TODO remove parents
//        println "Delete: $url"
        dbHandler.deletePath(url)
    }
}
