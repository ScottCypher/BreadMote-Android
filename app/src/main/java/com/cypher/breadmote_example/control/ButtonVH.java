package com.cypher.breadmote_example.control;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.ButtonComponent;

/**
 * Created by cypher1 on 1/23/16.
 */
class ButtonVH extends ComponentVH<ButtonComponent> implements View.OnClickListener {
    private final Button button;

    ButtonVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_button, listener);
        button = (Button) itemView.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void update(ButtonComponent component) {
        super.update(component);
        button.setText(component.getName());
    }

    @Override
    public void onClick(View v) {
        listener.onRemoteCommand(component);
    }
}
