package com.cypher.breadmote_example;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by cypher1 on 1/23/16.
 */
public abstract class WebUtils {
    private static Intent newEmailIntent(String address, String subject) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        return emailIntent;
    }

    public static void promptEmail(Context context) {
        Intent emailIntent = newEmailIntent(context.getString(R.string.email_address),
                context.getString(R.string.email_subject));
        try {
            context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.email_not_found, Toast.LENGTH_SHORT).show();
        }
    }
}
