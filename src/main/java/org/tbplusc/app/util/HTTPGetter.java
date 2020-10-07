package org.tbplusc.app.util;

import java.io.IOException;

import org.jsoup.Jsoup;

public class HTTPGetter {

    private HTTPGetter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns <code>org.jsoup.nodes.Document</code> from the given URL string.
     * 
     * #TODO
     * 
     * @param url The URL address with the desired document.
     * @return URL when the operation is successful; null otherwise.
     */
    public static org.jsoup.nodes.Document getDocumentFromUrl(String url) throws IOException {
        return Jsoup.connect(url).ignoreContentType(true).get();
    }

    public static String getBodyFromUrl(String url) throws IOException {
        return getDocumentFromUrl(url).body().text();
    }
}
