package server

import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.Paths

import static Server.RESOURCES_DIRECTORY

@CompileStatic
class TemplateManager {
    private Map<String, String> fileNameToTemplateMap

    TemplateManager(String... fileNames) {
        fileNameToTemplateMap = new TreeMap<>()

        fileNames.each {
            String template = new String(Files.readAllBytes(Paths.get("$RESOURCES_DIRECTORY${it}.html")))
            fileNameToTemplateMap.put(it, template)
        }
    }

    String getPage(String fileName, Map<String, String> patternToReplacementMap) {
        String page = fileNameToTemplateMap.get(fileName)

        patternToReplacementMap.each {
            page = page.replace(it.key, it.value)
        }

        page
    }
}
