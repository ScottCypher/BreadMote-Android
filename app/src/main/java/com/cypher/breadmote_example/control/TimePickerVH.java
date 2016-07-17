package com.cypher.breadmote_example.control;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.TimePickerComponent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by scypher on 2/21/16.
 */
class TimePickerVH extends ComponentVH<TimePickerComponent> implements TimePickerDialog.OnTimeSetListener {

    private final TextView txtTime;

    TimePickerVH(ViewGroup parent, InteractionListener listener) {
        super(parent, R.layout.item_time_picker, listener);

        this.txtTime = (TextView) itemView.findViewById(R.id.txtTime);
    }

    @Override
    public void update(TimePickerComponent component) {
        super.update(component);

        String timeStr = getTimeString();
        String text = itemView.getContext().getString(R.string.control_time_format, component.getName(), timeStr);

        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                TimePickerVH.this.onClick();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        int length = text.length();
        ss.setSpan(clickableSpan, length - timeStr.length(), length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        txtTime.setText(ss);
        txtTime.setMovementMethod(LinkMovementMethod.getInstance());
        txtTime.setHighlightColor(Color.TRANSPARENT);

        int colorLink = component.isEnabled() ? R.color.colorAccent : R.color.colorAccentFaded;
        txtTime.setLinkTextColor(txtTime.getContext().getResources().getColor(colorLink));
    }

    private String getTimeString() {
        int hours = component.getHour();
        int mins = component.getMinute();
        String time = String.format("%02d:%02d", hours, mins);

        Date _24hourDate;
        try {
            _24hourDate = new SimpleDateFormat("H:mm").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return new SimpleDateFormat("h:mm a").format(_24hourDate);
    }

    private void onClick() {
        int hour = component.getHour();
        int mins = component.getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(itemView.getContext(), this, hour, mins, false);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        component.setValue(hourOfDay, minute);
        update(component);
        listener.onRemoteCommand(component);
    }
}
