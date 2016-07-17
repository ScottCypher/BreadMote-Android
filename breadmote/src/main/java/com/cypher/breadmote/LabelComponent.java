package com.cypher.breadmote;

import java.util.regex.Pattern;

/**
 * Represents a Label that displays values sent from the hardware. This component cannot be interacted
 * with.
 */
public class LabelComponent extends Component<String> {
    private final Pattern FORMATTABLE = Pattern.compile("%(1\\$)?[sS]");

    /**
     * @param name The name this component should display. Name also functions as a formattable String
     * @param value The text to be inserted in or appended to name
     * @see #getFormattedName()
     */
    public LabelComponent(String name, String value) {
        super(Connection.TYPE_LABEL, name, value);
    }

    /**
     * @return {@code true} if this component's name matches the pattern {@literal "%(1\\$)?[sS]"}
     * e.g. {@literal "Temperature %s"} or {@literal ">>%1s<<"}
     */
    public boolean isNameFormattable() {
        return FORMATTABLE.matcher(getName()).find();
    }

    /**
     *
     * @return If {@link #getName()} is a formattable, this component's name
     * is formatted with this component's value as the argument. Otherwise, the component's
     * value is appended to its name
     * @see #isNameFormattable()
     */
    public String getFormattedName() {
        String name = getName();
        String value = getValue();

        if (isNameFormattable()) {
            return String.format(name, value);
        } else {
            return name + value;
        }
    }
}
