package org.tbplusc.app.talent.helper.parsers;

import java.io.IOException;
import org.tbplusc.app.util.HttpGetter;

public class IcyVeinsRemoteDataProvider implements IIcyVeinsDataProvider {
    private static final String ADDRESS_PREFIX = "https://www.icy-veins.com/heroes/";
    private static final String ADDRESS_POSTFIX = "-talents";

    public org.jsoup.nodes.Document getDocument(String heroName) throws IOException {
        return HttpGetter.getDocumentFromUrl(ADDRESS_PREFIX + heroName + ADDRESS_POSTFIX);
    }
}
