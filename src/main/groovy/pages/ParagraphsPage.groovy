package pages

import groovy.transform.CompileStatic
import server.TemplateManager
import server.beans.ParagraphBean

import static Server.PARAGRAPHS_PAGE_NAME

@CompileStatic
class ParagraphsPage extends Page {
    private Collection<ParagraphBean> paragraphs

    ParagraphsPage(TemplateManager templateManager, Collection<ParagraphBean> paragraphs) {
        super(templateManager)

        this.paragraphs = paragraphs
    }

    @Override
    String generatePage() {
        templateManager.getPage(PARAGRAPHS_PAGE_NAME, new TreeMap<String, String>().tap {
            put('{paragraphs}', prependIndents(generateParagraphs(), 4))
        })
    }

    private String generateParagraphs() {
        StringBuilder sb = new StringBuilder()

        paragraphs.each {
            String htmlParagraph = it.paragraph

            it.phrases.each {
                htmlParagraph = htmlParagraph.replaceFirst(it.phrase, "<span class=\"phrase\" data-corrected-phrase=\"$it.correctedPhrase\" data-type=\"$it.type\" data-translation=\"$it.translation\">$it.phrase</span>")
            }

            sb.append("<p data-path=\"$it.path\">$htmlParagraph</p>\n")
        }

        sb.toString()
    }
}
