package pages

import groovy.transform.CompileStatic
import server.beans.ParagraphBean

@CompileStatic
class GenericPage extends Page {
    private String htmlParagraphs

    GenericPage(Collection<ParagraphBean> paragraphs) {
        StringBuilder sb = new StringBuilder()

        paragraphs.each {
            String htmlParagraph = it.paragraph

            it.phrases.each {
                htmlParagraph = htmlParagraph.replaceFirst(it.phrase, "<span class=\"phrase\" data-corrected-phrase=\"$it.correctedPhrase\" data-type=\"$it.type\" data-translation=\"$it.translation\">$it.phrase</span>")
            }

            sb.append("<p data-path=\"$it.path\">$htmlParagraph</p>\n")
        }

        htmlParagraphs = sb.toString()
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
                activateGenericListeners();
            });
        </script>
    </head>
    <body>
        <section class="shell">
            <textarea name="text-field" id="text-field" class="text-field" rows="10"></textarea>
            <div id="phrase-details"></div>
            <button id="submit">Submit</button>
            <div id="paragraphs">
                ${prependIndents(htmlParagraphs, 4)}
            </div>
        </section>
    </body>
</html>"""
    }
}
