package com.example.sockettest.ui;

import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;
import com.example.sockettest.utils.Songs;
import com.google.common.collect.Lists;

public class LibraryView {
    private final String[] ADAPTER_FROM = new String[] {Song.TITLE_KEY, Song.ARTIST_KEY};
    private final int ADAPTER_RESOURCE = R.layout.list_item;
    private final int[] ADAPTER_TO = new int[] {R.id.list_item_title, R.id.list_item_artist};

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
        this.libraryAdapter = createAdapter(libraryList);
        this.searchAdapter = createAdapter(searchList);

        this.libraryView = (ListView)device.findViewById(R.id.library_view);
        this.libraryView.setAdapter(libraryAdapter);

        this.searchView = (ListView)device.findViewById(R.id.search_view);
        this.searchView.setAdapter(searchAdapter);
        this.searchView.setVisibility(View.GONE);

        this.currentArtist = (TextView) device.findViewById(R.id.current_artist);
        this.currentTitle = (TextView) device.findViewById(R.id.current_title);

        this.playerControls = new PlayerControls(device);
        new SearchBar(device, this);

        libraryView.setOnItemClickListener(new PlayClickListener(false));
        libraryView.setOnItemLongClickListener(new EnqueueClickListener(false));

        searchView.setOnItemClickListener(new PlayClickListener(true));
        searchView.setOnItemLongClickListener(new EnqueueClickListener(true));
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

    private SimpleAdapter createAdapter(final List<Map<String, String>> list) {
        return new SimpleAdapter(device, list, ADAPTER_RESOURCE, ADAPTER_FROM, ADAPTER_TO);
    }

    private class PlayClickListener implements OnItemClickListener {
        private final boolean fromSearch;

        public PlayClickListener(final boolean fromSearch) {
            this.fromSearch = fromSearch;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            device.play(position, fromSearch);
            playerControls.showPauseButton();
        }
    }

    private class EnqueueClickListener implements OnItemLongClickListener {
        private final boolean fromSearch;

        public EnqueueClickListener(final boolean fromSearch) {
            this.fromSearch = fromSearch;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            return device.enqueueSong(position, fromSearch);
        }
    }
}
