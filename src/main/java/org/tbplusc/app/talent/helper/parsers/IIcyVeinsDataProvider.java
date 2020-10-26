package org.tbplusc.app.talent.helper.parsers;

import java.io.IOException;

public interface IIcyVeinsDataProvider {
    public org.jsoup.nodes.Document getDocument(String heroName) throws IOException;
}
