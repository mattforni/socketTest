package com.example.sockettest;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.widget.TabHost.OnTabChangeListener;

import com.example.sockettest.music.Song;
import com.example.sockettest.music.SongManager;
import com.example.sockettest.music.SongManager.UnknownSongException;
import com.example.sockettest.network.Message;
import com.example.sockettest.ui.LibraryView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class Device extends Activity implements OnTabChangeListener {
    protected Map<String, String> currentSong;
    protected final SongManager songManager;
    // These are the player 'state' variables which should probably be composed
    protected int currentIndex, nextIndex;
    protected boolean playing, shuffle;
    protected LibraryView libraryView;

    private final Map<String, Integer> searchMap; // Map of concat title and artist to song index
    private final List<Song> searchResults;

    private String id;

    protected Device(final String id) {
        this.id = id;

        this.songManager = new SongManager(this);
        this.searchResults = Lists.newArrayListWithExpectedSize(127);
        this.searchMap = Maps.newHashMap();

        this.currentIndex = -1;
        this.nextIndex = 1;
        this.playing = false;
        this.shuffle = false;
    }

    public abstract boolean enqueueSong(int index, boolean fromSearch);
    public abstract boolean next();
    public abstract boolean pause();
    public abstract boolean play();
    public abstract boolean play(int index, boolean fromSearch);
    public abstract boolean previous();
    public abstract void publishMessage(Message message);
    public abstract void receiveMessage(Message message);

    public final String getId() {
        return id;
    }

    public final ImmutableList<Song> search(final String query) {
        searchResults.clear();
        for (final Entry<String, Integer> entry : searchMap.entrySet()) {
            if (entry.getKey().contains(query)) {
                searchResults.add(songManager.getSong(entry.getValue()));
            }
        }
        return ImmutableList.copyOf(searchResults);
    }

    public final void setId(final String id) {
        this.id = id;
    }

    public final void updateLibrary(final List<Song> songs) {
        songManager.updateLibrary(songs);
        libraryView.updateLibrary(songManager.getAllSongs());
    }

    protected final Song getSong(
            final int index, final boolean fromSearch) throws UnknownSongException {
        if (!fromSearch) { return songManager.getSong(index); }
        if (index < 0 || index >= searchResults.size()) { throw new UnknownSongException(index); }
        return searchResults.get(index);
    }
}
