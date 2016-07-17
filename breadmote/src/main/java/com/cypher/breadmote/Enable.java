package com.cypher.breadmote;

import android.content.Context;

/**
 * Created by scypher on 5/5/16.
 */
class Enable {
    private final String name;
    private final boolean isEnabled;

    Enable(String name, boolean isEnabled) {
        this.name = name;
        this.isEnabled = isEnabled;
    }

    boolean isFor(Component component) {
        return component.getName().equals(name);
    }

    boolean isEnabled() {
        return isEnabled;
    }

    Error generateError(Context context) {
        String tag = context.getString(isEnabled ? R.string.invalid_enable : R.string.invalid_disable);
        String message = context.getString(R.string.invalid_enable_message, name);
        return new Error(tag, message);
    }

    String getName() {
        return name;
    }
}
