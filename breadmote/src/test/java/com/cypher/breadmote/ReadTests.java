package com.cypher.breadmote;

import android.support.v4.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by cypher on 6/25/16.
 */
public class ReadTests {

    public static final MessageHeader[] MESSAGE_HEADERS = new MessageHeader[]{
            new MessageHeader(new byte[]{0, 0, 2, 2}),
            new MessageHeader(new byte[]{0, 0, 4, 4}),
            new MessageHeader(new byte[]{0, 1, 2, 2}),
            new MessageHeader(new byte[]{0, 1, 4, 4})
    };

    public static final int[] SAMPLED_INTS = new int[]{0, 50, -50, Short.MAX_VALUE, Short.MIN_VALUE};
    public static final String[] SAMPLED_STRINGS = new String[]{"something", "", "¬∆åß∂©•™¬…∆"};
    public static final boolean[] BOOLEANS = new boolean[]{true, false};
    public static final int[][] SEEK_VALUES = new int[][]{
            new int[]{0, 100, 50},
            new int[]{-100, 100, 0},
            new int[]{0, 100, 100},
            new int[]{0, 100, 0}
    };


    private static <T> void applyCheck(int type, List<Pair<MessageHeader, byte[]>> possibleMessages, Checker<T> checker) {
        for (Pair<MessageHeader, byte[]> messagePair : possibleMessages) {
            MessageHeader messageHeader = messagePair.first;
            byte[] message = messagePair.second;
            MessageAnalyzer messageAnalyzer = new MessageAnalyzer(null, messageHeader, type, message);
            T t = (T) messageAnalyzer.analyzeMessage();
            checker.check(t, messageHeader);
        }
    }

    @Test
    public void testHeader() {
        class HeaderInfo {
            private byte funcSize, intSize;
            private boolean isBigEndian;
            private byte version;

            public HeaderInfo(byte funcSize, byte intSize, boolean isBigEndian, byte version) {
                this.funcSize = funcSize;
                this.intSize = intSize;
                this.isBigEndian = isBigEndian;
                this.version = version;
            }
        }

        HeaderInfo[] headerInfos = new HeaderInfo[]{
                new HeaderInfo((byte) 2, (byte) 2, false, (byte) 0),
                new HeaderInfo((byte) 2, (byte) 2, true, (byte) 0),
                new HeaderInfo((byte) 4, (byte) 4, true, (byte) 0),
                new HeaderInfo((byte) 4, (byte) 4, false, (byte) 0),
        };

        for (HeaderInfo headerInfo : headerInfos) {
            byte[] bytes = new byte[]{
                    headerInfo.version,
                    (byte) (headerInfo.isBigEndian ? 1 : 0),
                    headerInfo.intSize,
                    headerInfo.funcSize
            };
            MessageAnalyzer messageAnalyzer = new MessageAnalyzer(null, MessageHeader.DEFAULT_HEADER, Connection.TYPE_HEADER, bytes);
            MessageHeader messageHeader = (MessageHeader) messageAnalyzer.analyzeMessage();
            Assert.assertEquals(headerInfo.funcSize, messageHeader.getSizeOfFunc());
            Assert.assertEquals(headerInfo.intSize, messageHeader.getSizeOfInt());
            Assert.assertEquals(headerInfo.isBigEndian, messageHeader.isBigEndian());
            Assert.assertEquals(headerInfo.version, messageHeader.getVersion());
        }
    }

    @Test
    public void testCreate() {
        for (final boolean b : BOOLEANS) {
            List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                    .add(b)
                    .buildPossibleValues();
            applyCheck(Connection.TYPE_CREATE, possibleMessages, new Checker<Creation>() {
                @Override
                public void check(Creation creation, MessageHeader messageHeader) {
                    Assert.assertEquals(b, creation.isCreating());
                }
            });
        }
    }

    @Test
    public void testError() {
        for (final String tag : SAMPLED_STRINGS) {
            for (final String message : SAMPLED_STRINGS) {
                List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                        .add(tag)
                        .add(message)
                        .buildPossibleValues();
                applyCheck(Connection.TYPE_ERROR, possibleMessages, new Checker<Error>() {
                    @Override
                    public void check(Error error, MessageHeader messageHeader) {
                        Assert.assertEquals(error.getTag(), tag);
                        Assert.assertEquals(error.getMessage(), message);
                    }
                });
            }
        }
    }

    @Test
    public void testRemove() {
        for (final String s : SAMPLED_STRINGS) {
            List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                    .add(s)
                    .buildPossibleValues();
            applyCheck(Connection.TYPE_REMOVE, possibleMessages, new Checker<Removal>() {
                @Override
                public void check(Removal removal, MessageHeader messageHeader) {
                    Assert.assertEquals(s, removal.getName());
                }
            });
        }
    }

    @Test
    public void testEnable() {
        for (final String s : SAMPLED_STRINGS) {
            for (final boolean b : BOOLEANS) {
                List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                        .add(s)
                        .add(b)
                        .buildPossibleValues();
                applyCheck(Connection.TYPE_ENABLE, possibleMessages, new Checker<Enable>() {
                    @Override
                    public void check(Enable enable, MessageHeader messageHeader) {
                        Assert.assertEquals(s, enable.getName());
                        Assert.assertEquals(b, enable.isEnabled());
                    }
                });
            }
        }
    }

    @Test
    public void testUpdate() {
        {
            for (final String s : SAMPLED_STRINGS) {
                for (final boolean b : BOOLEANS) {
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(s)
                            .add(b)
                            .buildPossibleValues();
                    applyCheck(Connection.TYPE_UPDATE, possibleMessages, new Checker<Update>() {
                        @Override
                        public void check(Update update, MessageHeader messageHeader) {
                            Assert.assertEquals(s, update.getName());
                            Assert.assertEquals(b, update.getValueAsBoolean());
                        }
                    });
                }
            }
        }

        {
            for (final String s : SAMPLED_STRINGS) {
                for (final int i : SAMPLED_INTS) {
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(s)
                            .add(i)
                            .buildPossibleValues();
                    applyCheck(Connection.TYPE_UPDATE, possibleMessages, new Checker<Update>() {
                        @Override
                        public void check(Update update, MessageHeader messageHeader) {
                            Assert.assertEquals(s, update.getName());
                            Assert.assertEquals(i, update.getValueAsInteger(messageHeader));
                        }
                    });
                }
            }
        }

        {
            for (final String s : SAMPLED_STRINGS) {
                for (final String updateStr : SAMPLED_STRINGS) {
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(s)
                            .add(updateStr)
                            .buildPossibleValues();
                    applyCheck(Connection.TYPE_UPDATE, possibleMessages, new Checker<Update>() {
                        @Override
                        public void check(Update update, MessageHeader messageHeader) {
                            Assert.assertEquals(s, update.getName());
                            Assert.assertEquals(updateStr, update.getValueAsString());
                        }
                    });
                }
            }
        }
    }

    @Test
    public void testSeek() {
        for (final String s : SAMPLED_STRINGS) {
            for (final int func : SAMPLED_INTS) {
                for (final int[] seekValue : SEEK_VALUES) {
                    final int min = seekValue[0];
                    final int max = seekValue[1];
                    final Integer value = seekValue[2];
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(s)
                            .add(func)
                            .add(min)
                            .add(max)
                            .add(value)
                            .buildPossibleValues();
                    applyCheck(Connection.TYPE_SLIDER, possibleMessages, new Checker<SliderComponent>() {
                        @Override
                        public void check(SliderComponent component, MessageHeader messageHeader) {
                            Assert.assertEquals(s, component.getName());
                            Assert.assertEquals(func, component.getFunc());
                            Assert.assertEquals(min, component.getMin());
                            Assert.assertEquals(max, component.getMax());
                            Assert.assertEquals(value, component.getValue());
                        }
                    });
                }
            }
        }
    }

    @Test
    public void testSwitch() {
        testBooleanComponent(Connection.TYPE_SWITCH);
    }

    @Test
    public void testCheckBox() {
        testBooleanComponent(Connection.TYPE_CHECKBOX);
    }

    private void testBooleanComponent(int type) {
        for (final String s : SAMPLED_STRINGS) {
            for (final int func : SAMPLED_INTS) {
                for (final boolean b : BOOLEANS) {
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(s)
                            .add(func)
                            .add(b)
                            .buildPossibleValues();
                    applyCheck(type, possibleMessages, new Checker<Component<Boolean>>() {
                        @Override
                        public void check(Component<Boolean> component, MessageHeader messageHeader) {
                            Assert.assertEquals(s, component.getName());
                            Assert.assertEquals(func, component.getFunc());
                            Assert.assertEquals(b, component.getValue());
                        }
                    });
                }
            }
        }
    }

    @Test
    public void testButton() {
        for (final String s : SAMPLED_STRINGS) {
            for (final int func : SAMPLED_INTS) {
                List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                        .add(s)
                        .add(func)
                        .buildPossibleValues();
                applyCheck(Connection.TYPE_BUTTON, possibleMessages, new Checker<Component<Boolean>>() {
                    @Override
                    public void check(Component<Boolean> component, MessageHeader messageHeader) {
                        Assert.assertEquals(s, component.getName());
                        Assert.assertEquals(func, component.getFunc());
                    }
                });
            }
        }
    }

    @Test
    public void testTextField() {
        for (final String s : SAMPLED_STRINGS) {
            for (final int func : SAMPLED_INTS) {
                for (final String val : SAMPLED_STRINGS) {
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(s)
                            .add(func)
                            .add(val)
                            .buildPossibleValues();
                    applyCheck(Connection.TYPE_TEXTFIELD, possibleMessages, new Checker<Component<String>>() {
                        @Override
                        public void check(Component<String> component, MessageHeader messageHeader) {
                            Assert.assertEquals(s, component.getName());
                            Assert.assertEquals(func, component.getFunc());
                            Assert.assertEquals(val, component.getValue());
                        }
                    });
                }
            }
        }
    }

    @Test
    public void testLabel() {
        for (final String s : SAMPLED_STRINGS) {
            for (final String val : SAMPLED_STRINGS) {
                List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                        .add(s)
                        .add(val)
                        .buildPossibleValues();
                applyCheck(Connection.TYPE_LABEL, possibleMessages, new Checker<Component<String>>() {
                    @Override
                    public void check(Component<String> component, MessageHeader messageHeader) {
                        Assert.assertEquals(s, component.getName());
                        Assert.assertEquals(val, component.getValue());
                    }
                });
            }
        }
    }

    @Test
    public void testTimePicker() {
        for (final String s : SAMPLED_STRINGS) {
            for (final int func : SAMPLED_INTS) {
                for (byte hour = 0; hour < 25; hour++) {
                    for (byte minute = 0; minute < 60; minute++) {
                        List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                                .add(s)
                                .add(func)
                                .add(hour)
                                .add(minute)
                                .buildPossibleValues();
                        final byte finalHour = hour;
                        final byte finalMinute = minute;
                        applyCheck(Connection.TYPE_TIMEPICKER, possibleMessages, new Checker<TimePickerComponent>() {
                            @Override
                            public void check(TimePickerComponent component, MessageHeader messageHeader) {
                                Assert.assertEquals(s, component.getName());
                                Assert.assertEquals(func, component.getFunc());
                                Assert.assertEquals(finalHour, component.getValue().getHour());
                                Assert.assertEquals(finalMinute, component.getValue().getMinute());
                            }
                        });
                    }
                }
            }
        }
    }

    @Test
    public void testRadio() {
        Random random = new Random("cypher".hashCode());
        String[][] allOptions = new String[][]{
                new String[]{"akjdfhs", "v890gj", "asjkdghaieug", "0124gfaklsbgn", "asiodgvyaefbg90"},
                new String[]{"only"},
                new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
                SAMPLED_STRINGS,
                new String[]{"ˆ¨£™ƒ∆", "ƒªƒ•™©ƒ˚¬∆"}
        };

        for (final String name : SAMPLED_STRINGS) {
            for (final int func : SAMPLED_INTS) {
                for (final String[] options : allOptions) {
                    final byte selected = (byte) random.nextInt(options.length);
                    int numOptions = options.length;
                    List<Pair<MessageHeader, byte[]>> possibleMessages = new MessageBuilder()
                            .add(name)
                            .add(func)
                            .add((byte) numOptions)
                            .add(options)
                            .add(selected)
                            .buildPossibleValues();
                    applyCheck(Connection.TYPE_RADIO_GROUP, possibleMessages, new Checker<RadioGroupComponent>() {
                        @Override
                        public void check(RadioGroupComponent component, MessageHeader messageHeader) {
                            Assert.assertEquals(name, component.getName());
                            Assert.assertEquals(func, component.getFunc());
                            Assert.assertArrayEquals(options, component.getOptions());
                            Assert.assertEquals(selected, (byte) (int) component.getValue());
                        }
                    });
                }
            }
        }
    }

    private interface Checker<T> {
        void check(T t, MessageHeader messageHeader);
    }

    private static class MessageBuilder {

        private final List<Object> objects = new LinkedList<>();

        private static byte[] toPrimitives(Byte[] bytes) {
            byte[] primBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                primBytes[i] = bytes[i];
            }
            return primBytes;
        }

        private static void add(List<Byte> byteList, byte[] bytes) {
            for (byte b : bytes) {
                byteList.add(b);
            }
        }

        MessageBuilder add(byte val) {
            objects.add(val);
            return this;
        }

        MessageBuilder add(int val) {
            objects.add(val);
            return this;
        }

        MessageBuilder add(String val) {
            objects.add(val);
            return this;
        }

        MessageBuilder add(boolean val) {
            objects.add(val);
            return this;
        }

        List<Pair<MessageHeader, byte[]>> buildPossibleValues() {
            byte[][] possibleMessages = new byte[MESSAGE_HEADERS.length][];
            for (int i = 0; i < MESSAGE_HEADERS.length; i++) {
                MessageHeader messageHeader = MESSAGE_HEADERS[i];

                List<Byte> payload = new LinkedList<>();
                for (Object o : objects) {
                    byte[] objBytes;
                    if (o instanceof Integer) {
                        objBytes = ByteConverter.toBytes((Integer) o, messageHeader);
                    } else if (o instanceof String) {
                        objBytes = ByteConverter.toBytes((String) o);
                    } else if (o instanceof Boolean) {
                        objBytes = ByteConverter.toBytes((Boolean) o);
                    } else if (o instanceof Byte) {
                        objBytes = new byte[]{(byte) o};
                    } else {
                        throw new RuntimeException("Unknown type: " + o.getClass());
                    }
                    add(payload, objBytes);
                }

                Byte[] byteArr = payload.toArray(new Byte[payload.size()]);
                possibleMessages[i] = toPrimitives(byteArr);
            }

            List<Pair<MessageHeader, byte[]>> pairs = new LinkedList<>();
            for (int i = 0; i < MESSAGE_HEADERS.length; i++) {
                MessageHeader messageHeader = MESSAGE_HEADERS[i];
                byte[] message = possibleMessages[i];
                pairs.add(new Pair<>(messageHeader, message));
            }
            return pairs;
        }

        public MessageBuilder add(String[] options) {
            for (String s : options) {
                add(s);
            }
            return this;
        }
    }
}
