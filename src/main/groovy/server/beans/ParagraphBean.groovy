package server.beans

import groovy.transform.CompileStatic

@CompileStatic
class ParagraphBean {
    String uuid
    String path
    String paragraph
    PhraseBean[] phrases
}
