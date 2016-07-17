package com.cypher.breadmote;

/**
 * Represents a slider (aka seekbar) used to interact with the hardware. This class also provides
 * several convenience methods for use with a {@link android.widget.SeekBar}. These methods help
 * reduce complexities that arise due to SeekBar's inability to set a minimum value other than 0.
 */
public class SliderComponent extends Component<Integer> {

    private final int min, max;

    /**
     *
     * @param name The name this component should display
     * @param func The function on the hardware to be called when this component is interacted with
     * @param min The minimum value this component can have
     * @param max The maximum value this component can have
     * @param value The current value of this component
     */
    public SliderComponent(String name, int func, int min, int max, int value) {
        super(Connection.TYPE_SLIDER, name, func, 0);
        this.min = min;
        this.max = max;
        setValue(value);
    }

    /**
     * A convenience method for setting the max value on a {@link android.widget.SeekBar}
     * @return The number of possible values
     */
    public int getRange() {
        return max - min;
    }

    /**
     * A convenience method for setting the progress value on a {@link android.widget.SeekBar}
     * @return The value used for {@link android.widget.SeekBar#setProgress(int)}
     */
    public int getProgress() {
        return getValue() - min;
    }

    /**
     * A convenience method for setting this component's value from the progress value of a {@link android.widget.SeekBar}
     * @param value The value returned from {@link android.widget.SeekBar#getProgress()}
     */
    public void setProgress(int value) {
        setValue(min + value);
    }

    /**
     * @return The Slider's maximum value
     */
    public int getMax() {
        return max;
    }

    @Override
    public void setValue(Integer value) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        super.setValue(value);
    }

    /**
     * @return The Slider's minimum value
     */
    public int getMin() {
        return min;
    }
}
