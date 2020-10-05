package org.tbplusc.app.validator_tests;

import org.junit.Test;
import org.tbplusc.app.Main;
import org.tbplusc.app.validator.Validator;

import static org.junit.Assert.assertEquals;

public class ValidatorTests {
    protected Validator validator = new Validator();

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
        assertEquals("Sgt. Hammer", validator.getClosestToInput("hammer"));
    }
}