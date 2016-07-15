package com.cypher.breadmote;

import android.content.Context;

/**
 * Created by cypher1 on 1/28/16.
 */
class Update {
    private final String name;
    private final byte[] value;
    private boolean failedApply;

    Update(String name, byte[] value) {
        this.name = name;
        this.value = value;
    }

    String getValueAsString() {
        return new String(value).replace("\0", "");
    }

    boolean getValueAsBoolean() {
        return value[0] != 0;
    }

    int getValueAsInteger(MessageHeader messageHeader) {
        return new MessageAnalyzer.MessageDecoder(messageHeader, value).readInt();
    }

    boolean canApplyTo(MessageHeader messageHeader, Component component) {
        Class componentClass = component.getValue().getClass();
        if (componentClass == Integer.class) {
            try {
                getValueAsInteger(messageHeader);
                return true;
            } catch (Exception ignored) {
                failedApply = true;
                return false;
            }
        } else if (componentClass == Boolean.class) {
            try {
                getValueAsBoolean();
                return true;
            } catch (Exception ignored) {
                failedApply = true;
                return false;
            }
        } else if (componentClass == String.class) {
            return true;
        } else {
            throw new RuntimeException("Unsupported component update: " + componentClass.getName());
        }
    }

    Error generateError(Context context) {
        String tag = context.getString(R.string.invalid_update_tag);
        String message = context.getString(failedApply ? R.string.invalid_update_message_2 : R.string.invalid_update_message, name);
        return new Error(tag, message);
    }

    String getName() {
        return name;
    }

    public TimePickerComponent.SimpleTime getValueAsSimpleTime() {
        return new TimePickerComponent.SimpleTime(value[0], value[1]);
    }
}
