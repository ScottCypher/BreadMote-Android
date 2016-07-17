package com.cypher.breadmote;

import android.content.Context;

/**
 * Created by cypher1 on 1/28/16.
 */
class Removal {
    private final String name;

    Removal(String name) {
        this.name = name;
    }

    boolean isFor(Component component) {
        return component.getName().equals(name);
    }

    Error generateError(Context context) {
        String tag = context.getString(R.string.invalid_delete);
        String message = context.getString(R.string.invalid_update_message, name);
        return new Error(tag, message);
    }

    String getName() {
        return name;
    }
}
