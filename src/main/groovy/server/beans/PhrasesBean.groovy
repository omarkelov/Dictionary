package server.beans

import groovy.transform.CompileStatic

@CompileStatic
class PhrasesBean {
    String uuid
    String path
    String text
    PhraseBean[] phrases
}
