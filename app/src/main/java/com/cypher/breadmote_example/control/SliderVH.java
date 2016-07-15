package com.cypher.breadmote_example.control;

import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.SliderComponent;

/**
 * Created by cypher1 on 1/23/16.
 */
class SliderVH extends ComponentVH<SliderComponent> implements SeekBar.OnSeekBarChangeListener {

    private final SeekBar seekBar;
    private final TextView textValue, textMax, label;

    SliderVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_seekbar, listener);
        seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        textValue = (TextView) itemView.findViewById(R.id.textValue);
        textMax = (TextView) itemView.findViewById(R.id.textMax);
        label = (TextView) itemView.findViewById(R.id.textView);
    }

    @Override
    public void update(SliderComponent component) {
        super.update(component);
        seekBar.setOnSeekBarChangeListener(null);

        seekBar.setMax(component.getRange());
        seekBar.setProgress(component.getProgress());
        textValue.setText(String.valueOf(component.getValue()));
        textMax.setText(String.valueOf(component.getMax()));
        label.setText(component.getName());

        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        component.setProgress(seekBar.getProgress());

        textValue.setText(String.valueOf(component.getValue()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        listener.onRemoteCommand(component);
    }
}
