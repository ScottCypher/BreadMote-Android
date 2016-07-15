package com.cypher.breadmote_example.control;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.Component;
import com.cypher.breadmote.ComponentListener;
import com.cypher.breadmote.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cypher1 on 1/23/16.
 */
class ComponentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ComponentListener {

    private List<Component> components;
    private boolean isCreating;

    private final InteractionListener interactionListener;

    public ComponentAdapter(InteractionListener interactionListener) {
        this.interactionListener = interactionListener;
        isCreating = true;
        components = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case Connection.TYPE_INVALID:
                return new EmptyVH(inflater.inflate(R.layout.item_component_empty, parent, false));
            case Connection.TYPE_SLIDER:
                return new SliderVH(parent, interactionListener);
            case Connection.TYPE_SWITCH:
                return new SwitchVH(parent, interactionListener);
            case Connection.TYPE_BUTTON:
                return new ButtonVH(parent, interactionListener);
            case Connection.TYPE_CHECKBOX:
                return new CheckBoxVH(parent, interactionListener);
            case Connection.TYPE_TEXTFIELD:
                return new TextfieldVH(parent, interactionListener);
            case Connection.TYPE_LABEL:
                return new LabelVH(parent);
            case Connection.TYPE_TIMEPICKER:
                return new TimePickerVH(parent, interactionListener);
            case Connection.TYPE_RADIO_GROUP:
                return new RadioVH(parent, interactionListener);
            default:
                throw new IllegalStateException("Unknown type should be displayed in the error log: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() != Connection.TYPE_INVALID) {
            ((ComponentVH) holder).update(components.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!isCreating && components.isEmpty()) {
            return Connection.TYPE_INVALID;
        } else {
            return components.get(position).getType();
        }
    }

    @Override
    public int getItemCount() {
        if (isCreating) {
            return components.size();
        } else {
            return components.isEmpty() ? 1 : components.size();
        }
    }

    @Override
    public void onComponentAdded(Component component) {
        int index = components.size() - 1;
        notifyItemInserted(index + getComponentStartIndex());
    }

    private int getComponentStartIndex() {
        return isCreating ? 1 : 0;
    }

    @Override
    public void onComponentUpdated(int index) {
        notifyItemChanged(index + getComponentStartIndex());
    }

    @Override
    public void onComponentRemoved(int index) {
        notifyItemRemoved(index);
    }

    @Override
    public void setComponents(List<Component> components) {
        this.components = components;
        notifyDataSetChanged();
    }

    @Override
    public void onComponentCreation(boolean isCreating) {
        if (this.isCreating != isCreating) {
            this.isCreating = isCreating;
            notifyDataSetChanged();
        }
    }

    private static class EmptyVH extends RecyclerView.ViewHolder {
        public EmptyVH(View itemView) {
            super(itemView);
        }
    }

}
