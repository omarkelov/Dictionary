package pages

import groovy.transform.CompileStatic

@CompileStatic
abstract class Page {
    protected static final String INDENT = '    '

    protected String prependIndents(String str, int n) {
        if (str && str[str.length() - 1] == '\n') {
            str = str.substring(0, str.length() - 1)
        }

        String indent = ''
        n.times {
            indent += INDENT
        }

        str.replaceAll('\n', "\n$indent")
    }

    abstract String getPage()
}
