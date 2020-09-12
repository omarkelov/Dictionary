package httphandlers.util

import groovy.transform.CompileStatic

import java.nio.charset.StandardCharsets

@CompileStatic
class HandlerUtils {
    static String decodeUrl(String url) {
        url ? URLDecoder.decode(url, StandardCharsets.UTF_8) : ''
    }

    static String trimLeadingSlashes(String str) {
        str ? str.replaceAll('^/+', '') : ''
    }
}
