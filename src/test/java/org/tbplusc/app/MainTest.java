package org.tbplusc.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void testMergeStringsCorrectly() {
        Main classUnderTest = new Main();
        assertEquals("MergedStrings", classUnderTest.mergeStrings(new String[] { "Merged", "Strings" }));
    }
}
