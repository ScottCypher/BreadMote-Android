package com.cypher.breadmote_example.error;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.BreadMote;
import com.cypher.breadmote.Connection;
import com.cypher.breadmote.ConnectionListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class ErrorActivityFragment extends Fragment implements ConnectionListener{

    private ErrorAdapter errorAdapter;
    private Connection connection;

    public ErrorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());        recyclerView.setLayoutManager(layoutManager);
        setupSwipe(recyclerView);

        errorAdapter = new ErrorAdapter();
        BreadMote.addListener(this);
        recyclerView.setAdapter(errorAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BreadMote.removeListener(this);
        if (connection != null) {
            connection.removeListener(errorAdapter);
        }
    }

    private void setupSwipe(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                connection.removeError(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect(Connection connection) {
        this.connection = connection;
        connection.addListener(errorAdapter);
    }
}
