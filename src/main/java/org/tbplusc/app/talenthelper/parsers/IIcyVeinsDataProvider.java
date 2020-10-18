package org.tbplusc.app.talenthelper.parsers;

import java.io.IOException;

public interface IIcyVeinsDataProvider {
    public org.jsoup.nodes.Document getDocument(String heroName) throws IOException;
}
