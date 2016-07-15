package com.cypher.breadmote_example.control;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cypher.breadmote.Component;

/**
 * Created by cypher1 on 1/23/16.
 */
abstract class ComponentVH<T extends Component> extends RecyclerView.ViewHolder {

    final InteractionListener listener;
    T component;

    ComponentVH(ViewGroup parent, int layoutId, InteractionListener listener) {
        super(inflate(parent, layoutId));
        this.listener = listener;
    }

    private static View inflate(ViewGroup parent, int layoutId) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return layoutInflater.inflate(layoutId, parent, false);
    }

    private static void setEnabled(View view, boolean isEnabled) {
        view.setEnabled(isEnabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setEnabled(child, isEnabled);
            }
        }
    }

    void update(T component) {
        this.component = component;
        setEnabled(itemView, component.isEnabled());
    }
}
