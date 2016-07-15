package com.cypher.breadmote;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by scypher on 7/10/16.
 */
public class LabelComponentTest {
    @Test
    public void isFormattable() throws Exception {
        isFormattable("%s");
    }

    @Test
    public void isFormattable2() throws Exception {
        isFormattable("%S");
    }

    @Test
    public void isFormattable3() throws Exception {
        isFormattable("%1$s");
    }

    @Test
    public void isFormattable4() throws Exception {
        isFormattable("%1$S");
    }

    @Test
    public void isFormattable5() throws Exception {
        isFormattable("Temperature %s");
    }

    @Test
    public void isFormattable6() throws Exception {
        isFormattable("%s temperature");
    }

    @Test
    public void isFormattable7() throws Exception {
        isFormattable("__%s__");
    }

    @Test
    public void isNotFormattable() throws Exception {
        isNotFormattable("%d");
    }

    @Test
    public void isNotFormattable2() throws Exception {
        isNotFormattable("Test");
    }

    @Test
    public void isNotFormattable3() throws Exception {
        isNotFormattable("");
    }

    @Test
    public void isNotFormattable4() throws Exception {
        isNotFormattable("˚˙å√∂øˆ√∆¬å˚∂¬");
    }

    private void isFormattable(String s) {
        LabelComponent labelComponent = new LabelComponent(s, null);
        Assert.assertTrue(labelComponent.isNameFormattable());
    }

    private void isNotFormattable(String s) {
        LabelComponent labelComponent = new LabelComponent(s, null);
        Assert.assertFalse(labelComponent.isNameFormattable());
    }
}
