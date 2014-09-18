package com.example.sockettest.ui;

import java.util.List;
import java.util.Map;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.Source;
import com.example.sockettest.ui.listener.PlayClickListener;
import com.example.sockettest.utils.Songs;
import com.example.sockettest.utils.UI;
import com.google.common.collect.Lists;

public class PlaylistView {
    private final ListView playlistView;
    private final List<Map<String, String>> playlistList;
    private final SimpleAdapter playlistAdapter;

    public PlaylistView(final Device device) {
        this.playlistList = Lists.newArrayList();
        this.playlistAdapter = UI.createSongListAdapter(device, playlistList);

        this.playlistView = (ListView)device.findViewById(R.id.playlist_view);
        this.playlistView.setAdapter(playlistAdapter);

        playlistView.setOnItemClickListener(new PlayClickListener(device, Source.PLAYLIST));
    }

    // TODO Enqueues a list of songs
    public final void updatePlaylist(final List<Song> enqueuedSongs) {
        playlistList.clear();
        playlistList.addAll(Songs.toListOfMaps(enqueuedSongs));
        playlistAdapter.notifyDataSetChanged();
    }
}
