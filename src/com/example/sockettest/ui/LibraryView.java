package com.example.sockettest.ui;

import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.Source;
import com.example.sockettest.ui.listener.EnqueueClickListener;
import com.example.sockettest.ui.listener.PlayClickListener;
import com.example.sockettest.utils.Songs;
import com.example.sockettest.utils.UI;
import com.google.common.collect.Lists;

public class LibraryView {
    private final Device device;
    private final ListView libraryView, searchView;
    private final List<Map<String, String>> libraryList, searchList;
    private final PlayerControls playerControls;
    private final SimpleAdapter libraryAdapter, searchAdapter;
    private final TextView currentArtist, currentTitle;

    public LibraryView(final Device device) {
        this.device = device;

        this.libraryList = Lists.newArrayList();
        this.searchList = Lists.newArrayList();
        this.libraryAdapter = UI.createSongListAdapter(device, libraryList);
        this.searchAdapter = UI.createSongListAdapter(device, searchList);

        this.libraryView = (ListView)device.findViewById(R.id.library_view);
        this.libraryView.setAdapter(libraryAdapter);

        this.searchView = (ListView)device.findViewById(R.id.search_view);
        this.searchView.setAdapter(searchAdapter);
        this.searchView.setVisibility(View.GONE);

        this.currentArtist = (TextView) device.findViewById(R.id.current_artist);
        this.currentTitle = (TextView) device.findViewById(R.id.current_title);


        this.playerControls = device.isServer() ? new PlayerControls(device) : null;
        new SearchBar(device, this);

        libraryView.setOnItemClickListener(new PlayClickListener(device, Source.LIBRARY));
        libraryView.setOnItemLongClickListener(new EnqueueClickListener(device, Source.LIBRARY));

        searchView.setOnItemClickListener(new PlayClickListener(device, Source.SEARCH));
        searchView.setOnItemLongClickListener(new EnqueueClickListener(device, Source.SEARCH));
    }

    public final void showPauseButton() {
        if (playerControls != null) { playerControls.showPauseButton(); }
    }

    public final void showPlayButton() {
        if (playerControls != null) { playerControls.showPlayButton(); }
    }

    public final void showSearch(final String query) {
        searchList.addAll(Songs.toListOfMaps(device.search(query)));
        searchAdapter.notifyDataSetChanged();
        libraryView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

    public final void hideSearch() {
        searchList.clear();
        searchAdapter.notifyDataSetChanged();
        searchView.setVisibility(View.GONE);
        libraryView.setVisibility(View.VISIBLE);
    }

    public final void updateCurrentSong(final Song song) {
        currentArtist.setText(song.getArtist());
        currentTitle.setText(song.getTitle());

        currentArtist.setSelected(true);
        currentTitle.setSelected(true);
    }

    public final void updateLibrary(final List<Song> library) {
        libraryList.clear();
        libraryList.addAll(Songs.toListOfMaps(library));
        libraryAdapter.notifyDataSetChanged();
    }
}
