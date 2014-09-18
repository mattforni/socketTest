package com.example.sockettest.ui.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.sockettest.Device;
import com.example.sockettest.music.Source;

public class EnqueueClickListener implements OnItemLongClickListener {
    private final Device device;
    private final Source source;

    public EnqueueClickListener(final Device device, final Source source) {
        this.device = device;
        this.source = source;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        return device.enqueueSong(source, position);
    }
}
