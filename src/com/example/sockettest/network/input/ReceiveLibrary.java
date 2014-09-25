package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.util.List;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;

public class ReceiveLibrary extends InputMessage {
    private final List<Song> library;

    public ReceiveLibrary(final List<Song> library) {
        this.library = library;
    }

    @Override
    public void receive(final Device device) {
        if (library == null) {
            device.updateLibrary(library);
            Log.i(tag(this), format("Updating library"));
        } else {
            Log.e(tag(this), "Tried to update library");
        }
    }
}
