package com.cypher.breadmote;

import android.content.Context;

/**
 * Represents a time picker that can be used to send a simple 24 hour time value to the hardware.
 */
public class TimePickerComponent extends Component<TimePickerComponent.SimpleTime> {
    /**
     * @param name The name this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     * @param hour The hour for this component (on a 24 hour clock)
     * @param minute The minute for this component
     */
    public TimePickerComponent(String name, int func, int hour, int minute) {
        super(Connection.TYPE_TIMEPICKER, name, func, new SimpleTime(hour, minute));
    }

    TimePickerComponent(String name, int func, SimpleTime simpleTime) {
        super(Connection.TYPE_TIMEPICKER, name, func, simpleTime);
    }

    static Error generateError(int tagId, String name, Context context) {
        String tag = context.getString(tagId);
        String message = context.getString(R.string.invalid_create_time_message, name);
        return new Error(tag, message);
    }

    /**
     * @param hour The new hour for this component
     * @param minute The new minute for this component
     */
    public void setValue(int hour, int minute) {
        setValue(new SimpleTime(hour, minute));
    }

    /**
     * @return The hour for this component
     */
    public int getHour() {
        return getValue().getHour();
    }

    /**
     *
     * @return The minute for this component
     */
    public int getMinute() {
        return getValue().getMinute();
    }

    /**
     * Represents a 24 hour time value
     */
    public static class SimpleTime implements Byteable {
        private final int hour, minute;

        /**
         * @param hour The hour (on a 24 hour clock)
         * @param minute The minute
         */
        public SimpleTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        /**
         *
         * @return The hour (on a 24 hour clock)
         */
        public int getHour() {
            return hour;
        }

        /**
         *
         * @return The minute
         */
        public int getMinute() {
            return minute;
        }

        boolean isValid() {
            return hour <= 24 && hour >= 0 && minute <= 60 && minute >= 0;
        }

        @Override
        public byte[] getBytes(MessageHeader messageHeader) {
            return new byte[]{(byte) hour, (byte) minute};
        }
    }
}
