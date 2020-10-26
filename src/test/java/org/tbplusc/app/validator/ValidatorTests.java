package org.tbplusc.app.validator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ValidatorTests {
    protected Validator validator = new Validator(new ArrayList<>(Arrays.asList("Mei", "Rexxar", "Abathur", "Chen", "Cho", "Raynor")));

    @Test
    public void testRetursCorrectAnswerOnCompleteInput() {
        assertEquals("Mei", validator.getClosestToInput("Mei"));
    }

    @Test
    public void testReturnsCorrectAnswerOnCloseInput1() {
        assertEquals("Mei", validator.getClosestToInput("Mai"));
    }

    @Test
    public void testReturnsCorrectAnswerOnCloseInput2() {
        assertEquals("Rexxar", validator.getClosestToInput("raxar"));
    }

    @Test
    public void testReturnsCorrectAnswerOnCloseInput3() {
        assertEquals("Abathur", validator.getClosestToInput("abatyr"));
    }

    @Test
    public void testReturnsCorrectAnswerOnCloseInput4() {
        assertEquals("Chen", validator.getClosestToInput("cen"));
    }

    @Test
    public void testReturnsCorrectAnswerOnCloseInput5() {
        assertEquals("Cho", validator.getClosestToInput("Cho"));
    }

//    @Test
//    public void testReturnsCorrectAnswerOnCloseInput5() {
//        assertEquals("Sgt. Hammer", validator.getClosestToInput("hammer"));
//    }
}
