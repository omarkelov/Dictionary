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
        dbHandler.insertPath(url)
    }

    void deletePage(String url) {
//        println "Delete: $url"
        dbHandler.getPaths().each {
            println it
        }
    }
}
