package com.cypher.breadmote;

/**
 * Represents a text field that can be used to send a string to the hardware.
 */
public class TextFieldComponent extends Component<String> {
    /**
     * @param name The name or hint this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     * @param value The text to be displayed inside the text field
     */
    public TextFieldComponent(String name, int func, String value) {
        super(Connection.TYPE_TEXTFIELD, name, func, value);
    }
}
