package com.cypher.breadmote_example.control;

import android.view.ViewGroup;
import android.widget.TextView;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.LabelComponent;

/**
 * Created by cypher1 on 1/23/16.
 */
class LabelVH extends ComponentVH<LabelComponent> {

    private final TextView textView;

    LabelVH(ViewGroup parent) {
        super(parent, R.layout.item_label, null);
        textView = (TextView) itemView.findViewById(R.id.textView);
    }

    @Override
    public void update(LabelComponent component) {
        super.update(component);

        textView.setText(component.getFormattedName());
    }
}
