package com.cypher.breadmote;

/**
 * Represents a button used to interact with the hardware.
 */
public class ButtonComponent extends Component<Boolean> {
    /**
     * @param name The name this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     */
    public ButtonComponent(String name, int func) {
        super(Connection.TYPE_BUTTON, name, func, false);
    }
}
