package pages

import groovy.transform.CompileStatic
import server.TemplateManager
import util.PathTree

import static Server.MAIN_PAGE_NAME
import static Server.SITE_NAME

@CompileStatic
class MainPage extends Page {
    private PathTree pathTree
    private StringBuilder sb

    MainPage(TemplateManager templateManager, Collection <String> paths) {
        super(templateManager)

        pathTree = new PathTree(level: 0, name: SITE_NAME).tap {
            insertAll(paths)
        }
    }

    @Override
    String generatePage() {
        templateManager.getPage(MAIN_PAGE_NAME, new TreeMap<String, String>().tap {
            put('{pathList}', prependIndents(generatePathList(), 3)) // TODO get rid of prependIndents()
        })
    }

    private String generatePathList() {
        sb = new StringBuilder()
        sb.append('<ul id="outer-list" class="outer-list">\n')

        int currentLevel = 0
        int indent = 1
        pathTree.each {
            if (it.level > 0) {
                (currentLevel - it.level + 1).times {
                    concatWithIndent('</ul>\n', --indent)
                    concatWithIndent('</li>\n', --indent)
                }
            }

            concatWithIndent("<li><a href=\"${it.getPath()}\">$it.name</a><span class=\"button button-create\">(+)</span><span class=\"button button-delete\">(×)</span>\n", indent++)
            concatWithIndent('<ul>\n', indent++)

            currentLevel = it.level
        }

        (currentLevel + 1).times {
            concatWithIndent('</ul>\n', --indent)
            concatWithIndent('</li>\n', --indent)
        }
        concatWithIndent('</ul>\n', --indent)

        sb.toString()
    }

    private void concatWithIndent(String str, int indents) {
        indents.times {sb.append(INDENT)}
        sb.append(str)
    }
}
