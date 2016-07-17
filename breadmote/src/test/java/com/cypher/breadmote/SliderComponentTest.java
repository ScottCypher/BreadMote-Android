package com.cypher.breadmote;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by scypher on 4/21/16.
 */
public class SliderComponentTest {
    @Test
    public void seekComponentTest() throws Exception {
        valueTest(0, 20000);
    }

    @Test
    public void seekComponentTest2() throws Exception {
        valueTest(20, 20000);
    }

    @Test
    public void seekComponentTest3() throws Exception {
        valueTest(-20000, 20000);
    }

    @Test
    public void seekComponentTest4() throws Exception {
        valueTest(-20000, -20);
    }

    @Test
    public void seekComponentTest5() throws Exception {
        valueTest(-20000, 0);
    }

    private void valueTest(int min, int max) {
        SliderComponent sliderComponent = new SliderComponent(null, 0, min, max, 0);

        for (int i = min; i < max; i++) {
            sliderComponent.setValue(i);
            Assert.assertTrue(sliderComponent.getProgress() == (i - min));
            Assert.assertTrue(sliderComponent.getValue() == i);
        }
    }
}
