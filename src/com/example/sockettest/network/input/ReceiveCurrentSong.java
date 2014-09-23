package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;

public class ReceiveCurrentSong extends InputMessage {
    private final Song song;

    public ReceiveCurrentSong(final Song song) {
        this.song = song;
    }

    @Override
    public void receive(final Device device) {
        if (song != null) {
            device.updateCurrentSong(song);
            Log.i(tag(this), format("Current song updated: %s", song.toString()));
        } else {
            Log.e(tag(this), "Tried to update current song");
        }
    }
}
