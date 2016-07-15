package com.cypher.breadmote_example.control;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.CheckBoxComponent;

/**
 * Created by cypher1 on 1/23/16.
 */
class CheckBoxVH extends ComponentVH<CheckBoxComponent> implements CompoundButton.OnCheckedChangeListener {

    private final CheckBox checkBox;
    private boolean isProgramattic;

    CheckBoxVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_checkbox, listener);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void update(CheckBoxComponent component) {
        super.update(component);
        isProgramattic = true;
        checkBox.setChecked(component.getValue());
        isProgramattic = false;
        checkBox.setText(component.getName());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isProgramattic) {
            component.setValue(isChecked);
            listener.onRemoteCommand(component);
        }
    }
}
