package com.cypher.breadmote_example.control;

import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.SwitchComponent;

/**
 * Created by cypher1 on 1/23/16.
 */
class SwitchVH extends ComponentVH<SwitchComponent> implements CompoundButton.OnCheckedChangeListener {

    private final Switch aSwitch;
    private boolean isProgramattic;

    SwitchVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_switch, listener);
        aSwitch = (Switch) itemView.findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void update(SwitchComponent component) {
        super.update(component);
        isProgramattic = true;
        aSwitch.setChecked(component.getValue());
        isProgramattic = false;
        aSwitch.setText(component.getName());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isProgramattic) {
            component.setValue(isChecked);
            listener.onRemoteCommand(component);
        }
    }
}
