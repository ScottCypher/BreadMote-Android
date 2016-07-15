package com.cypher.breadmote_example.connect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cypher.breadmote_example.R;
import com.cypher.breadmote.Device;
import com.cypher.breadmote.ScanListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cypher1 on 1/23/16.
 */
class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ScanListener {

    private static final int VIEW_REFRESHING = 0, VIEW_ITEM = 1, VIEW_EMPTY = 2;
    private final ConnectListener connectListener;
    private final List<Device> devices;
    private boolean isScanning;
    private int lastCount;

    public DeviceAdapter(ConnectListener connectListener) {
        this.connectListener = connectListener;
        devices = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_EMPTY:
                return new EmptyVH(inflater.inflate(R.layout.item_device_empty, parent, false));
            case VIEW_REFRESHING:
                return new RefreshVH(inflater.inflate(R.layout.item_device, parent, false));
            case VIEW_ITEM:
                return new DeviceVH(inflater.inflate(R.layout.item_device, parent, false),
                        connectListener);
            default:
                throw new IllegalArgumentException("Unknown viewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_ITEM) {
            DeviceVH deviceVH = (DeviceVH) holder;
            deviceVH.update(devices.get(position - (isScanning ? 1 : 0)));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isScanning && position == 0) {
            return VIEW_REFRESHING;
        } else if (!isScanning && devices.isEmpty()) {
            return VIEW_EMPTY;
        } else {
            return VIEW_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        int count;
        if (isScanning) {
            count = devices.size() + 1;
        } else {
            count = devices.isEmpty() ? 1 : devices.size();
        }
        lastCount = count;
        return count;
    }

    @Override
    public void onScanStateChange(boolean isScanning) {
        if (this.isScanning != isScanning) {
            this.isScanning = isScanning;
            if (isScanning) {
                notifyItemRangeRemoved(0, lastCount);

                devices.clear();
                notifyItemInserted(0);
            } else {
                notifyItemRemoved(0);
            }
        }
    }

    @Override
    public void onDeviceFound(Device device) {
        devices.add(device);
        int index = devices.size() - 1;
        notifyItemInserted(index + getDeviceStartIndex());
    }

    private int getDeviceStartIndex() {
        return isScanning ? 1 : 0;
    }

    @Override
    public void setCurrentDevices(List<Device> devices) {
        this.devices.clear();
        notifyItemRangeRemoved(0, lastCount);
        this.devices.addAll(devices);
        int startIndex = getDeviceStartIndex();
        notifyItemRangeInserted(startIndex, devices.size());
    }

    public interface ConnectListener {
        void connect(Device device);
    }

    private static class DeviceVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name, mac;
        private final ConnectListener connectListener;
        private Device device;

        public DeviceVH(View itemView, ConnectListener connectListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.textView);
            mac = (TextView) itemView.findViewById(R.id.textView2);
            this.connectListener = connectListener;
        }

        public void update(Device device) {
            this.device = device;
            this.name.setText(device.getName());
            mac.setText(device.getAddress());
        }

        @Override
        public void onClick(View v) {
            connectListener.connect(device);
        }
    }

    private static class EmptyVH extends RecyclerView.ViewHolder {
        public EmptyVH(View itemView) {
            super(itemView);
        }
    }

    public static class RefreshVH extends RecyclerView.ViewHolder {
        public RefreshVH(View itemView) {
            super(itemView);
        }
    }
}
