package com.cypher.breadmote;

import junit.framework.Assert;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by cypher on 6/25/16.
 */
public class WriteTests {

    private static final boolean[] BOOLEANS = ReadTests.BOOLEANS;
    private static final MessageHeader[] MESSAGE_HEADERS = ReadTests.MESSAGE_HEADERS;
    private static final String[] SAMPLED_STRINGS = ReadTests.SAMPLED_STRINGS;
    private static final int[] SAMPLED_INTS = ReadTests.SAMPLED_INTS;
    private static final int[][] SEEK_VALUES = ReadTests.SEEK_VALUES;

    private static void assertAsExpected(byte actualType, TLVCompatible tlvCompatible, List<Object> expecteds) {
        for (MessageHeader messageHeader : MESSAGE_HEADERS) {
            byte[] bytes = tlvCompatible.getTLV(messageHeader);
            byte type = bytes[0];
            byte[] payload = Arrays.copyOfRange(bytes, 3, bytes.length);

            Assert.assertEquals(actualType, type);
            MessageAnalyzer.MessageDecoder messageDecoder = new MessageAnalyzer.MessageDecoder(messageHeader, payload);
            for (Object expected : expecteds) {
                Object o;
                if (expected instanceof Integer) {
                    o = messageDecoder.readInt();
                } else if (expected instanceof String) {
                    o = messageDecoder.readString();
                } else if (expected instanceof Boolean) {
                    o = messageDecoder.readBoolean();
                } else if (expected instanceof Character) {
                    o = (char) messageDecoder.readByte();
                } else {
                    throw new RuntimeException("Unknown type: " + expected.getClass());
                }
                Assert.assertEquals(expected, o);
            }
        }
    }

    private static short getLength(byte[] bytes) {
        return ByteBuffer.wrap(bytes, 1, 2).getShort();
    }

    @Test
    public void testHeader() {
        Header header = new Header();
        byte[] bytes = header.getTLV(MessageHeader.DEFAULT_HEADER);
        byte type = bytes[0];
        short length = getLength(bytes);
        Assert.assertEquals(Connection.TYPE_HEADER, type);
        Assert.assertEquals(0, length);
    }

    @Test
    public void testCreate() {
        for (boolean b : BOOLEANS) {
            Creation creation = new Creation(b);
            List<Object> expecteds = new LinkedList<>();
            expecteds.add(b);
            assertAsExpected(Connection.TYPE_CREATE, creation, expecteds);
        }
    }

    @Test
    public void testSeek() {
        for (int func : SAMPLED_INTS) {
            for (int[] values : SEEK_VALUES) {
                int value = values[2];
                Component<Integer> component = new SliderComponent(null, func, values[0], values[1], value);
                List<Object> expecteds = new LinkedList<>();
                expecteds.add(func);
                expecteds.add(value);
                assertAsExpected(Connection.TYPE_SLIDER, component, expecteds);
            }
        }
    }

    @Test
    public void testSwitch() {
        testBoolean(new ComponentCreator() {
            @Override
            public Component<Boolean> createComponent(int func, boolean value) {
                return new SwitchComponent(null, func, value);
            }
        });
    }

    private void testBoolean(ComponentCreator componentCreator) {
        for (int func : SAMPLED_INTS) {
            for (boolean value : BOOLEANS) {
                Component<Boolean> component = componentCreator.createComponent(func, value);
                List<Object> expecteds = new LinkedList<>();
                expecteds.add(func);
                expecteds.add(value);
                assertAsExpected(component.getType(), component, expecteds);
            }
        }
    }

    @Test
    public void testButton() {
        testBoolean(new ComponentCreator() {
            @Override
            public Component<Boolean> createComponent(int func, boolean value) {
                Component<Boolean> booleanComponent = new ButtonComponent(null, func);
                booleanComponent.setValue(value);
                return booleanComponent;
            }
        });
    }

    @Test
    public void testCheckBox() {
        testBoolean(new ComponentCreator() {
            @Override
            public Component<Boolean> createComponent(int func, boolean value) {
                return new CheckBoxComponent(null, func, value);
            }
        });
    }

    @Test
    public void testTextField() {
        for (int func : SAMPLED_INTS) {
            for (String value : SAMPLED_STRINGS) {
                Component<String> component = new TextFieldComponent(null, func, value);
                List<Object> expecteds = new LinkedList<>();
                expecteds.add(func);
                expecteds.add(value);
                assertAsExpected(Connection.TYPE_TEXTFIELD, component, expecteds);
            }
        }
    }

    @Test
    public void testTimePicker() {
        for (int func : SAMPLED_INTS) {
            for (char hour = 0; hour < 25; hour++) {
                for (char minute = 0; minute < 60; minute++) {
                    TimePickerComponent.SimpleTime simpleTime = new TimePickerComponent.SimpleTime(hour, minute);
                    Assert.assertTrue(simpleTime.isValid());
                    Component<TimePickerComponent.SimpleTime> component = new TimePickerComponent(null, func, simpleTime);
                    List<Object> expecteds = new LinkedList<>();
                    expecteds.add(func);
                    expecteds.add(hour);
                    expecteds.add(minute);
                    assertAsExpected(Connection.TYPE_TIMEPICKER, component, expecteds);
                }
            }
        }
    }

    @Test
    public void testRadio() {
        Random random = new Random("cypher".hashCode());
        for (int func : SAMPLED_INTS) {
            for (int i = 1; i < 0xFF; i++) {
                String[] options = new String[i];
                int selected = random.nextInt(options.length);
                Component<Integer> component = new RadioGroupComponent(null, func, selected, options);
                List<Object> expecteds = new LinkedList<>();
                expecteds.add(func);
                expecteds.add(selected);
                assertAsExpected(Connection.TYPE_RADIO_GROUP, component, expecteds);
            }
        }
    }

    interface ComponentCreator {
        Component<Boolean> createComponent(int func, boolean value);
    }

}