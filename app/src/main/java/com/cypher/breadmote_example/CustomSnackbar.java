package com.cypher.breadmote_example;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by cypher on 5/27/16.
 */
public final class CustomSnackbar {
    private CustomSnackbar() {
    }

    public static Snackbar make(View view, String text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);

        View snackView = snackbar.getView();
        snackView.setBackgroundResource(R.color.snackbar_background);

        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        int textColor = view.getContext().getResources().getColor(R.color.snackbar_text);
        tv.setTextColor(textColor);

        TextView tv2 = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_action);
        int textColor2 = view.getContext().getResources().getColor(R.color.snackbar_action);
        tv2.setTextColor(textColor2);

        return snackbar;
    }
}
