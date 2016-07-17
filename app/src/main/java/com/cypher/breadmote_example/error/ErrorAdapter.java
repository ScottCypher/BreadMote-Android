package com.cypher.breadmote_example.error;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.Error;
import com.cypher.breadmote.ErrorListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cypher1 on 1/24/16.
 */
class ErrorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ErrorListener {

    private static final int VIEW_EMPTY = 0, VIEW_ITEM = 1;
    private List<Error> errors;

    public ErrorAdapter() {
        errors = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_ITEM:
                View itemView = inflater.inflate(R.layout.item_error, parent, false);
                return new ErrorVH(itemView);
            case VIEW_EMPTY:
                View emptyView = inflater.inflate(R.layout.item_error_empty, parent, false);
                return new EmptyVH(emptyView);
            default:
                throw new IllegalArgumentException("Unknown viewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_ITEM) {
            Error error = errors.get((errors.size() - 1) - position);
            ErrorVH errorVH = (ErrorVH) holder;
            errorVH.update(error);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return errors.isEmpty() ? VIEW_EMPTY : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return errors.isEmpty() ? 1 : errors.size();
    }

    @Override
    public void onError(Error error) {
        if (errors.size() == 1) {
            notifyItemRemoved(0);
        }

        notifyItemInserted(errors.size() - 1);
    }

    @Override
    public void onErrorRemoved(int index) {
        notifyItemRemoved(index);

        if (errors.isEmpty()) {
            notifyItemInserted(0);
        }
    }

    @Override
    public void setErrors(List<Error> errors) {
        this.errors = errors;
        notifyDataSetChanged();
    }

    private static class ErrorVH extends RecyclerView.ViewHolder {

        private final TextView textView, textView2;

        ErrorVH(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            textView2 = (TextView) itemView.findViewById(R.id.textView2);
        }

        void update(Error error) {
            Date date = error.getTimeStamp();
            String timeString = DateFormat.getDateTimeInstance().format(date);

            textView.setText(timeString);
            String errorText = itemView.getContext().getString(R.string.error_html, error.getTag(), error.getMessage());
            textView2.setText(Html.fromHtml(errorText));
        }
    }

    private static class EmptyVH extends RecyclerView.ViewHolder {
        EmptyVH(View itemView) {
            super(itemView);
        }
    }
}
