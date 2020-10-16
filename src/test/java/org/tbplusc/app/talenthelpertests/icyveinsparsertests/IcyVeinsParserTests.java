package org.tbplusc.app.talenthelpertests.icyveinsparsertests;

import org.junit.Test;
import org.tbplusc.app.talenthelper.parsers.IIcyVeinsDataProvider;
import org.tbplusc.app.talenthelper.parsers.IcyVeinsTalentProvider;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.jsoup.Jsoup;


public class IcyVeinsParserTests {
    private static final String TEST_HTML_PATH = "html";

    private org.jsoup.nodes.Document docWithOneBuild;
    private org.jsoup.nodes.Document docWithMultipleBuilds;
    private IcyVeinsTalentProvider parser;

    @Before
    public void setUp() throws IOException {
        var mock = Mockito.mock(IIcyVeinsDataProvider.class);
        parser = new IcyVeinsTalentProvider(mock);
        docWithOneBuild = Jsoup.parse(new String(getClass().getClassLoader()
                        .getResourceAsStream(TEST_HTML_PATH + "/icyveins_example_build.html")
                        .readAllBytes()));
        docWithMultipleBuilds = Jsoup.parse(new String(getClass().getClassLoader()
                        .getResourceAsStream(
                                        TEST_HTML_PATH + "/icyveins_example_multiple_builds.html")
                        .readAllBytes()));
        Mockito.when(mock.getDocument("testheroone")).thenReturn(docWithOneBuild);
        Mockito.when(mock.getDocument("testherotwo")).thenReturn(docWithMultipleBuilds);
    }

    @Test
    public void testCorrectBuildSize() throws IOException {
        var oneBuild = parser.getBuilds("testheroone");
        var twoBuilds = parser.getBuilds("testherotwo");
        assertEquals(1, oneBuild.getBuilds().size());
        assertEquals(2, twoBuilds.getBuilds().size());
    }

    @Test
    public void testCorrectBuildName() throws IOException {
        var buildToTest = parser.getBuilds("testherotwo").getBuilds().get(1);
        assertEquals("Situational Test Build", buildToTest.getName());
    }

    @Test
    public void testCorrectOrderOfTalents() throws IOException {
        var buildToTest = parser.getBuilds("testheroone").getBuilds().get(0);
        assertArrayEquals(new String[] {"Single Talent 1", "Single Talent 2", "Single Talent 3",
            "Single Talent 4", "Single Talent 5", "Single Talent 6", "Single Talent 7"},
                        buildToTest.getTalents().toArray());
    }

    @Test
    public void testCorrectDescription() throws IOException {
        var buildToTest = parser.getBuilds("testherotwo").getBuilds().get(0);
        assertEquals("Generic multi-purpose build for test hero.", buildToTest.getDescription());
    }

    @Test
    public void testCorrectHeroName() throws IOException {
        var heroBuildsToTest = parser.getBuilds("testheroone");
        assertEquals("Hero with one build", heroBuildsToTest.getHeroName());
    }
}
