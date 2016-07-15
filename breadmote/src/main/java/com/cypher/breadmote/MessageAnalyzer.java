package com.cypher.breadmote;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by cypher1 on 1/28/16.
 */
class MessageAnalyzer {

    private final Context context;
    private final MessageDecoder messageDecoder;
    private final int type;

    public MessageAnalyzer(Context context, MessageHeader messageHeader, int type, byte[] bytes) {
        this.context = context;
        this.type = type;
        this.messageDecoder = new MessageDecoder(messageHeader, bytes);
    }

    Object analyzeMessage() {
        switch (type) {
            case Connection.TYPE_HEADER:
                return createHeader();
            case Connection.TYPE_CREATE:
                return createInitializer();
            case Connection.TYPE_REMOVE:
                return createRemoval();
            case Connection.TYPE_ENABLE:
                return createEnable();
            case Connection.TYPE_BUTTON:
                return createButton();
            case Connection.TYPE_SWITCH:
                return createSwitch();
            case Connection.TYPE_CHECKBOX:
                return createCheckbox();
            case Connection.TYPE_TEXTFIELD:
                return createTextField();
            case Connection.TYPE_LABEL:
                return createLabel();
            case Connection.TYPE_TIMEPICKER:
                return createClock();
            case Connection.TYPE_RADIO_GROUP:
                return createRadio();
            case Connection.TYPE_UPDATE:
                return createUpdate();
            case Connection.TYPE_ERROR:
                return createUserError();
            case Connection.TYPE_SLIDER:
                return createSlider();
            default:
                return createUnknownMessage();
        }
    }

    private Object createHeader() {
        byte[] headerBytes = new byte[]{
                messageDecoder.readByte(),
                messageDecoder.readByte(),
                messageDecoder.readByte(),
                messageDecoder.readByte()
        };
        return new MessageHeader(headerBytes);
    }

    private Object createEnable() {
        String name = messageDecoder.readString();
        boolean isEnabled = messageDecoder.readBoolean();
        return new Enable(name, isEnabled);
    }

    private Object createInitializer() {
        boolean isBeginning = messageDecoder.readBoolean();
        return new Creation(isBeginning);
    }

    private Object createUnknownMessage() {
        String tag = context.getString(R.string.unsupported_component_tag);
        String message = context.getString(R.string.unsupported_component_message, type);
        return new Error(tag, message);
    }

    private Object createSlider() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        int min = messageDecoder.readInt();
        int max = messageDecoder.readInt();

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        int value = messageDecoder.readInt();
        return new SliderComponent(name, func, min, max, value);
    }

    private Object createUserError() {
        String tag = messageDecoder.readString();
        String message = messageDecoder.readString();
        return new Error(tag, message);
    }

    private Object createUpdate() {
        String name = messageDecoder.readString();
        byte[] value = messageDecoder.readFinalString();
        return new Update(name, value);
    }

    private Object createLabel() {
        String name = messageDecoder.readString();
        String value = messageDecoder.readString();

        return new LabelComponent(name, value);
    }

    private Object createClock() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        byte hours = messageDecoder.readByte();
        byte minutes = messageDecoder.readByte();

        TimePickerComponent.SimpleTime simpleTime = new TimePickerComponent.SimpleTime(hours, minutes);
        if (simpleTime.isValid()) {
            return new TimePickerComponent(name, func, simpleTime);
        } else {
            return TimePickerComponent.generateError(R.string.invalid_create_tag, name, context);
        }
    }

    private Object createRadio() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        int numOptions = messageDecoder.readByte();
        String[] options = new String[numOptions];

        for (int i = 0; i < numOptions; i++) {
            options[i] = messageDecoder.readString();
        }

        int selected = messageDecoder.readByte();

        return new RadioGroupComponent(name, func, selected, options);
    }

    private Object createTextField() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        String value = messageDecoder.readString();
        return new TextFieldComponent(name, func, value);
    }

    private Object createCheckbox() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        boolean value = messageDecoder.readBoolean();
        return new CheckBoxComponent(name, func, value);
    }

    private Object createSwitch() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        boolean value = messageDecoder.readBoolean();
        return new SwitchComponent(name, func, value);
    }

    private Object createButton() {
        String name = messageDecoder.readString();
        int func = messageDecoder.readFunc();
        return new ButtonComponent(name, func);
    }

    private Object createRemoval() {
        String name = messageDecoder.readString();
        return new Removal(name);
    }

    /**
     * Created by cypher1 on 1/28/16.
     */
    static class MessageDecoder {

        private static final String TAG = "MessageDecoder";
        private final MessageHeader messageHeader;
        private final byte[] bytes;
        private int index;

        MessageDecoder(MessageHeader messageHeader, byte[] bytes) {
            this.messageHeader = messageHeader == null ? MessageHeader.DEFAULT_HEADER : messageHeader;
            this.bytes = bytes;
        }

        boolean readBoolean() {
            return readByte() != 0;
        }

        byte readByte() {
            return bytes[index++];
        }

        int readInt() {
            if (messageHeader.getSizeOfInt() == 2) {
                return readShort();
            } else {
                return readIntHelper();
            }
        }

        private int readIntHelper() {
            int x = ByteBuffer.wrap(bytes, index, 4).order(messageHeader.getByteOrder()).getInt();
            index += 4;
            return x;
        }

        private short readShort() {
            short s = ByteBuffer.wrap(bytes, index, 2).order(messageHeader.getByteOrder()).getShort();
            index += 2;
            return s;
        }

        int readFunc() {
            if (messageHeader.getSizeOfFunc() == 2) {
                return readShort();
            } else {
                return readIntHelper();
            }
        }

        String readString() {
            int i = 0;
            while (index + i < bytes.length) {
                byte b = bytes[index + i];
                if (b == '\0') {
                    break;
                }
                i++;
            }
            if (index + i == bytes.length) {
                Log.e(TAG, "message end not reached");
            }

            byte[] array = Arrays.copyOfRange(bytes, index, index + i);
            index += i + 1;//drop terminating character
            return new String(array);
        }

        //used to read strings that could include terminating characters
        byte[] readFinalString() {
            return Arrays.copyOfRange(bytes, index, bytes.length);
        }

    }
}
