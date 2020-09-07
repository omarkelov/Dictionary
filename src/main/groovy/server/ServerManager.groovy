package server

import groovy.transform.CompileStatic

@CompileStatic
class ServerManager {
    void createPage(String url) {
        println "Create: $url"
    }

    void deletePage(String url) {
        println "Delete: $url"
    }
}
