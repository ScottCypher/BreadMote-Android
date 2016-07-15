package com.cypher.breadmote;

/**
 * Represents a radio group (exclusive set of options) specified from the hardware.
 */
public class RadioGroupComponent extends Component<Integer> {

    private final String[] options;

    /**
     * @param name The name this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     * @param value Index of the currently selected option
     * @param options The options that can be chosen from
     */
    public RadioGroupComponent(String name, int func, int value, String[] options) {
        super(Connection.TYPE_RADIO_GROUP, name, func, 0);
        this.options = options;

        setValue(value);
    }

    /**
     * @return The options that can be chosen from
     */
    public String[] getOptions() {
        return options;
    }

    @Override
    public void setValue(Integer integer) {
        if (integer < 0) {
            integer = 0;
        } else if (integer > options.length - 1) {
            integer = options.length - 1;
        }
        super.setValue(integer);
    }
}
