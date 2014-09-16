package com.example.sockettest.ui;

import java.util.List;
import java.util.Map;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;
import com.example.sockettest.utils.Songs;
import com.google.common.collect.Lists;

public class PlaylistView {
    private final String[] ADAPTER_FROM = new String[] {Song.TITLE_KEY, Song.ARTIST_KEY};
    private final int ADAPTER_RESOURCE = R.layout.list_item;
    private final int[] ADAPTER_TO = new int[] {R.id.list_item_title, R.id.list_item_artist};

    private final Device device;
    private final ListView playlistView;
    private final List<Map<String, String>> playlistList;
    private final SimpleAdapter playlistAdapter;
    
    public PlaylistView(final Device device) {
        this.device = device;

        this.playlistList = Lists.newArrayList();
        this.playlistAdapter = createAdapter(playlistList);

        this.playlistView = (ListView)device.findViewById(R.id.playlist_view);
        this.playlistView.setAdapter(playlistAdapter);

//        playlistView.setOnItemClickListener(new PlayClickListener(false));
    }

    // TODO Enqueues a list of songs
    public final void updatePlaylist(final List<Song> enqueuedSongs) {
        playlistList.clear();
        playlistList.addAll(Songs.toListOfMaps(enqueuedSongs));
        playlistAdapter.notifyDataSetChanged();
    }

    private SimpleAdapter createAdapter(final List<Map<String, String>> list) {
        return new SimpleAdapter(device, list, ADAPTER_RESOURCE, ADAPTER_FROM, ADAPTER_TO);
    }
}
