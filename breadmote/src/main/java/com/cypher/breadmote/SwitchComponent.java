package com.cypher.breadmote;

/**
 * Represents a switch used to interact with the hardware.
 */
public class SwitchComponent extends Component<Boolean> {
    /**
     * @param name The name this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     * @param value {@code true} if the switch is on, {@code false} otherwise
     */
    public SwitchComponent(String name, int func, boolean value) {
        super(Connection.TYPE_SWITCH, name, func, value);
    }
}
