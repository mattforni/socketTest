package com.example.sockettest.utils;

import java.util.List;
import java.util.Map;

import android.widget.SimpleAdapter;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;

public abstract class UI {
    private static final String[] SONG_FROM = new String[] {Song.TITLE_KEY, Song.ARTIST_KEY};
    private static final int SONG_RESOURCE = R.layout.list_item;
    private static final int[] SONG_TO = new int[] {R.id.list_item_title, R.id.list_item_artist};

    public static final SimpleAdapter createSongListAdapter(
            final Device device, final List<Map<String, String>> list) {
        return new SimpleAdapter(device, list, SONG_RESOURCE, SONG_FROM, SONG_TO);
    }
}
