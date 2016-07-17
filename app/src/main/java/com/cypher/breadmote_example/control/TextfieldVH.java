package com.cypher.breadmote_example.control;

import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.TextFieldComponent;

/**
 * Created by cypher1 on 1/23/16.
 */
class TextfieldVH extends ComponentVH<TextFieldComponent> implements TextView.OnEditorActionListener {

    private final TextInputLayout textInputLayout;
    private final EditText editText;

    TextfieldVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_textfield, listener);
        textInputLayout = (TextInputLayout) itemView.findViewById(R.id.textInputLayout);
        editText = (EditText) itemView.findViewById(R.id.editText);
        editText.setOnEditorActionListener(this);
    }

    @Override
    public void update(TextFieldComponent component) {
        super.update(component);
        textInputLayout.setHint(component.getName());
        editText.setText(component.getValue());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            component.setValue(v.getText().toString());
            listener.onRemoteCommand(component);
            //clear cursor from view
            editText.post(new Runnable() {
                @Override
                public void run() {
                    if (editText != null) {
                        editText.clearFocus();
                    }
                }
            });
        }
        //don't consume event to hide keyboard
        return false;
    }
}
