package pages

import groovy.transform.CompileStatic
import util.PathTree

import static Server.SITE_NAME

@CompileStatic
class MainPage extends Page {
    private String outerList

    MainPage(Collection<String> paths) {
        PathTree pathTree = new PathTree(level: 0, name: SITE_NAME).tap {
            insertAll(paths)
        }

        outerList = '<ul id="outer-list" class="outer-list">\n'

        int currentLevel = 0
        int indent = 1
        pathTree.each {
            if (it.level > 0) {
                (currentLevel - it.level + 1).times {
                    concatWithIndent(--indent, '</ul>\n')
                    concatWithIndent(--indent, '</li>\n')
                }
            }

            concatWithIndent(indent++, "<li><a href=\"${it.getPath()}\">$it.name</a><span class=\"button button-create\">(+)</span><span class=\"button button-delete\">(×)</span>\n")
            concatWithIndent(indent++, '<ul>\n')

            currentLevel = it.level
        }

        (currentLevel + 1).times {
            concatWithIndent(--indent, '</ul>\n')
            concatWithIndent(--indent, '</li>\n')
        }
        concatWithIndent(--indent, '</ul>\n')
    }

    private void concatWithIndent(int indents, String str) {
        indents.times {outerList += INDENT}
        outerList += str
    }

    @Override
    String getPage() {
"""<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Dictionary</title>
        <link rel="shortcut icon" href="/images/icon.png" type="image/x-icon">
        <link rel="icon" href="/images/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" href="/css/reset.css">
        <link rel="stylesheet" href="/css/main.css">
        <script src="/js/libs/jquery-3.5.1.min.js"></script>
        <script src="/js/main.js"></script>
        <script>
            \$(document).ready(function() {
                activateListeners();
            });
        </script>
    </head>
    <body>
        <section class="shell">
            ${prependIndents(outerList, 3)}
        </section>
    </body>
</html>"""
    }
}
