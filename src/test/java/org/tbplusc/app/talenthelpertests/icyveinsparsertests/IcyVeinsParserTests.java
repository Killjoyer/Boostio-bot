package org.tbplusc.app.talenthelpertests.icyveinsparsertests;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;

import org.tbplusc.app.talenthelper.icyveinsparser.IcyVeinsParser;


public class IcyVeinsParserTests {
    private static final String TEST_HTML_PATH = "html";

    private org.jsoup.nodes.Document docWithOneBuild;
    private org.jsoup.nodes.Document docWithMultipleBuilds;

    @Before
    public void setUp() throws IOException {
        docWithOneBuild = Jsoup.parse(new String(getClass().getClassLoader()
                        .getResourceAsStream(TEST_HTML_PATH + "/icyveins_example_build.html")
                        .readAllBytes()));
        docWithMultipleBuilds = Jsoup.parse(new String(getClass().getClassLoader()
                        .getResourceAsStream(
                                        TEST_HTML_PATH + "/icyveins_example_multiple_builds.html")
                        .readAllBytes()));
    }

    @Test
    public void testCorrectBuildNumbers() {
        var oneBuild = IcyVeinsParser.getBuildsListFromDocument(docWithOneBuild);
        var twoBuilds = IcyVeinsParser.getBuildsListFromDocument(docWithMultipleBuilds);
        assertEquals(1, oneBuild.size());
        assertEquals(2, twoBuilds.size());
    }

    @Test
    public void testCorrectBuildName() {
        var buildToTest = IcyVeinsParser.getBuildsListFromDocument(docWithMultipleBuilds).get(1);
        assertEquals("Situational Test Build", buildToTest.getName());
    }

    @Test
    public void testCorrectOrderOfTalents() {
        var buildToTest = IcyVeinsParser.getBuildsListFromDocument(docWithOneBuild).get(0);
        assertArrayEquals(new String[] {"Single Talent 1", "Single Talent 2", "Single Talent 3",
            "Single Talent 4", "Single Talent 5", "Single Talent 6", "Single Talent 7"},
                        buildToTest.getTalents().toArray());
    }

    @Test
    public void testCorrectDescription() {
        var buildToTest = IcyVeinsParser.getBuildsListFromDocument(docWithMultipleBuilds).get(0);
        assertEquals("Generic multi-purpose build for test hero.", buildToTest.getDescription());
    }
}
