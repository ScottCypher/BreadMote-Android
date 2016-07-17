package com.cypher.breadmote;

import android.content.Context;


/**
 * The root class for all types of components that the connected device can request.
 */
public abstract class Component<Value> extends TLVCompatible {

    private final String name;
    private Value value;
    private boolean isEnabled;
    private int func;
    private final byte type;

    Component(byte type, String name, Value value) {
        super(type);
        this.type = type;
        this.name = name;
        this.value = value;
        isEnabled = true;
    }

    Component(byte type, String name, int func, Value value) {
        super(type);
        this.type = type;
        this.name = name;
        this.value = value;
        isEnabled = true;
        this.func = func;
    }

    int getFunc() {
        return func;
    }

    /**
     * @return The type of this component. See {@link Connection} for possible values
     */
    public byte getType() {
        return type;
    }

    @Override
    protected byte[] getValue(MessageHeader messageHeader) {
        return ByteConverter.toBytes(ByteConverter.toBytes(func, messageHeader),
                getValueBytes(messageHeader));
    }

    boolean isAnUpdate(MessageHeader messageHeader, Update update) {
        Value startValue = getValue();
        Value newValue = getValueFromUpdate(messageHeader, update);
        if (startValue.equals(newValue)) {
            return false;
        } else {
            setValue(newValue);
            return true;
        }
    }

    Value getValueFromUpdate(MessageHeader messageHeader, Update update) {
        if (value instanceof Boolean) {
            Boolean value = update.getValueAsBoolean();
            return (Value) value;
        } else if (value instanceof Integer) {
            Integer value = update.getValueAsInteger(messageHeader);
            return (Value) value;
        } else if (value instanceof String) {
            return (Value) update.getValueAsString();
        } else if (value instanceof TimePickerComponent.SimpleTime) {
            return (Value) update.getValueAsSimpleTime();
        } else{
            throw new IllegalStateException("Trying to convert value of unknown type");
        }
    }

    /**
     * @return The name this component should display
     */
    public String getName() {
        return name;
    }

    /**
     * @return The current value of this component
     */
    public Value getValue() {
        return value;
    }

    /**
     * @param value The new value of this component
     */
    public void setValue(Value value) {
        this.value = value;
    }

    private byte[] getValueBytes(MessageHeader messageHeader) {
        if (value instanceof Boolean) {
            return ByteConverter.toBytes((Boolean) value);
        } else if (value instanceof Integer) {
            return ByteConverter.toBytes((Integer) value, messageHeader);
        } else if (value instanceof String) {
            return ByteConverter.toBytes((String) value);
        } else if (value instanceof Byteable) {
            return ((Byteable)value).getBytes(messageHeader);
        } else {
            throw new IllegalStateException("Trying to convert value of unknown type");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Component)) return false;

        Component component = (Component) object;
        return type == component.getType()
                && name.equals(component.getName())
                && value.equals(component.getValue());
    }

    Error generateError(Context context) {
        String tag = context.getString(R.string.invalid_create_tag);
        String message = context.getString(R.string.invalid_create_message, name);
        return new Error(tag, message);
    }

    /**
     * @return {@code true} if this component can be interacted with, {@code false} otherwise
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    void update(MessageHeader messageHeader, Update update) {
        Value value = getValueFromUpdate(messageHeader, update);
        setValue(value);
    }
}
