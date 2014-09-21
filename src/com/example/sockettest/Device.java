package com.example.sockettest;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.example.sockettest.music.Song;
import com.example.sockettest.music.SongManager;
import com.example.sockettest.music.Source;
import com.example.sockettest.music.Source.UnknownSongException;
import com.example.sockettest.ui.LibraryView;
import com.example.sockettest.ui.PlaylistView;

public abstract class Device extends Activity implements OnTabChangeListener {
    public static final String ADDRESS_KEY = "ADDRESS";
    public static final String PORT_KEY = "PORT";

    protected final SongManager songManager;

    protected String id;
    protected boolean isServer;
    protected Map<String, String> currentSong;
    protected LibraryView libraryView;
    protected PlaylistView playlistView;

    protected Device(final String id) {
        this.id = id;

        this.songManager = new SongManager(this);
        this.isServer = false;
    }

    public abstract boolean enqueueSong(Source source, int index);
    public abstract boolean next();
    public abstract boolean pause();
    public abstract boolean play();
    public abstract boolean play(Source source, int index);
    public abstract boolean previous();
    public abstract void setId(String id);

    public final String getId() {
        return id;
    }

    public final List<Song> search(final String query) {
        return songManager.search(query);
    }

    public final boolean isServer() {
    	return isServer;
    }

    @Override
    public void onTabChanged(final String tabId) {
        if(tabId.equals("tab1")){
        } else if(tabId.equals("tab2")){
        } else{
        }
    }

    public final void updateLibrary(final List<Song> songs) {
        Source.LIBRARY.update(songs);
        libraryView.updateLibrary(songManager.getAllSongs());
    }

    protected final Song getSong(final Source source, final int index) throws UnknownSongException {
        return songManager.getSong(source, index);
    }

    protected void initializeTabs() {
        TabHost tabs = (TabHost)findViewById(R.id.tabhost);

        tabs.setup();
        TabSpec spec = tabs.newTabSpec("tab1");
        spec.setContent(R.id.library);
        spec.setIndicator("LIBRARY");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tab2");
        spec.setContent(R.id.playlist);
        spec.setIndicator("PLAYLIST");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tab3");
        spec.setContent(R.id.settings);
        spec.setIndicator("SETTINGS");
        tabs.addTab(spec);

        tabs.setOnTabChangedListener(this);
     }
}
