package com.cypher.breadmote_example.connect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cypher.breadmote_example.R;

/**
 * Created by scott on 5/2/2015.
 */
public class WiFiConnectionInfoDialog extends DialogFragment implements TextView.OnEditorActionListener {

    private static final String EXTRA_PASSWORD = "extra_password", EXTRA_PORT = "extra_port";
    private static final String EXTRA_HOST = "extra_host";
    private EditText hostText, passText, portText;
    private final Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            Activity activity = getActivity();
            if (activity != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(hostText, 0);
                }
            }
        }
    };

    public static WiFiConnectionInfoDialog newInstance(String host, String password, int port) {
        Bundle args = new Bundle();
        args.putString(EXTRA_HOST, host);
        args.putString(EXTRA_PASSWORD, password);
        args.putInt(EXTRA_PORT, port);
        WiFiConnectionInfoDialog fragment = new WiFiConnectionInfoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_connection_info, null, false);


        hostText = (EditText) view.findViewById(R.id.host);

        portText = (EditText) view.findViewById(R.id.port);

        passText = (EditText) view.findViewById(R.id.password);
        passText.setOnEditorActionListener(this);

        Bundle args = getArguments();
        if (args != null) {
            hostText.setText(args.getString(EXTRA_HOST));
            portText.setText(String.valueOf(args.getInt(EXTRA_PORT)));
            passText.setText(args.getString(EXTRA_PASSWORD));
        }

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.show_password);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransformationMethod transformationMethod;
                if (checkBox.isChecked()) {
                    transformationMethod = null;
                } else {
                    transformationMethod = new PasswordTransformationMethod();
                }
                passText.setTransformationMethod(transformationMethod);
                passText.setSelection(passText.getText().length());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                forwardInput(hostText, passText, portText);
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        hostText.post(new Runnable() {
            @Override
            public void run() {
                if (hostText != null) {
                    hostText.requestFocus();
                    setImeVisibility(true);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hostText = null;
        passText = null;
        portText = null;
    }

    private void setImeVisibility(boolean visible) {
        if (visible) {
            hostText.post(mShowImeRunnable);
        } else {
            hostText.removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(hostText.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            forwardInput(hostText, passText, portText);
            dismiss();
            return true;
        }
        return false;
    }

    private void forwardInput(TextView hostView, TextView passView, TextView portView) {
        String host = getText(hostView);
        String password = getText(passView);
        String port = getText(portView);
        ((ConnectionInfoListener) getActivity()).onConnectionInfoSubmitted(host, password, port);
    }

    private String getText(TextView textView) {
        return TextUtils.isEmpty(textView.getText()) ? null : textView.getText().toString();
    }

    public interface ConnectionInfoListener {
        void onConnectionInfoSubmitted(String host, String password, String port);
    }
}
