package com.cypher.breadmote_example.control;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.RadioGroupComponent;

/**
 * Created by scypher on 3/5/16.
 */
class RadioVH extends ComponentVH<RadioGroupComponent> implements RadioGroup.OnCheckedChangeListener {

    private final TextView textView;
    private final RadioGroup radioGroup;
    private boolean isProgramattic;

    RadioVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_radio, listener);
        textView = (TextView) itemView.findViewById(R.id.textView);
        radioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void update(RadioGroupComponent component) {
        super.update(component);
        isProgramattic = true;

        textView.setText(component.getName());
        int selected = component.getValue();

        String[] options = component.getOptions();

        radioGroup.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio_item, radioGroup, false);
            radioButton.setText(option);
            if (i == selected) {
                radioButton.setChecked(true);
            }
            radioButton.setId(i + 1);
            radioGroup.addView(radioButton);
            radioButton.setEnabled(radioGroup.isEnabled());
        }
        isProgramattic = false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!isProgramattic) {
            int index = checkedId - 1;
            component.setValue(index);
            listener.onRemoteCommand(component);
        }
    }
}