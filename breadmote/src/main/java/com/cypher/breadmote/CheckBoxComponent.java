package com.cypher.breadmote;

/**
 * Represents a checkbox used to interact with the hardware.
 */
public class CheckBoxComponent extends Component<Boolean> {
    /**
     * @param name The name this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     * @param value {@code true} if the checkbox is checked, {@code false} otherwise
     */
    public CheckBoxComponent(String name, int func, boolean value) {
        super(Connection.TYPE_CHECKBOX, name, func, value);
    }
}
