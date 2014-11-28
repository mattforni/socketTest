package com.example.sockettest.ui.listener;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.sockettest.Device;
import com.example.sockettest.music.Source;

public class PlayClickListener implements OnItemClickListener {
    private final Device device;
    private final Source source;

    public PlayClickListener(final Device device, final Source source) {
        this.device = device;
        this.source = source;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(tag(this), format("Clicked on song '%s' with index %d", source.get(position).getTitle(), position));
        device.play(source, source.get(position));
    }
}
