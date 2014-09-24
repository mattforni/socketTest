package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.util.List;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.example.sockettest.ui.runnable.UpdateCurrentSongRunnable;
import com.example.sockettest.ui.runnable.UpdateLibraryRunnable;

public class ReceivePlaylist extends InputMessage {
    private final List<Song> playlist;

    public ReceivePlaylist(final List<Song> playlist) {
        this.playlist = playlist;
    }

    @Override
    public void receive(final Device device) {
        if (playlist != null) {
        	device.updatePlaylist(playlist);
            Log.i(tag(this), format("Updating Playlist"));
        } else {
            Log.e(tag(this), "Tried to update Playlist");
        }
    }
}
