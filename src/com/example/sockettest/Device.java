package com.example.sockettest;

import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.SongManager;
import com.example.sockettest.music.Source;
import com.example.sockettest.music.Source.UnknownSongException;
import com.example.sockettest.ui.LibraryView;
import com.example.sockettest.ui.PlaylistView;
import com.example.sockettest.ui.SettingsView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressLint("NewApi")
public abstract class Device extends Activity implements OnTabChangeListener {
    public static final String ADDRESS_KEY = "ADDRESS";
    public static final String PORT_KEY = "PORT";

    protected final SongManager songManager;

    protected String id;
    protected boolean isServer;
    protected Map<String, String> currentSong;
    protected LibraryView libraryView;
    protected PlaylistView playlistView;
    protected SettingsView settingsView;
    protected int port;
    protected String address;

    protected Device(final String id) {
        this.id = id;
        this.songManager = new SongManager(this);
        this.isServer = false;
    }
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.device_view);
        getActionBar().hide();
    }
    
    public abstract boolean enqueueSong(Source source, int index);
    public abstract boolean next();
    public abstract boolean pause();
    public abstract boolean play();
    public abstract boolean play(Source source, Song song);
    public abstract boolean previous();
    public abstract void receiveClientId(String id);

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
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);

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

        updateTabs(tabs);
        
        tabs.setOnTabChangedListener(new OnTabChangeListener() {
        	public void onTabChanged(String tabId) {
        		updateTabs(tabs);
            }
        });
    }
    
    private void updateTabs(TabHost tabs) {
        for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
        	tabs.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.black));
        	((TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(getResources().getColor(R.color.light_grey));
        }
        
        tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.light_grey));
        ((TextView) tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).findViewById(android.R.id.title)).setTextColor(getResources().getColor(R.color.black));
    }
    
    public int getPort() {
    	return port;
    }
    public String getAddress() {
    	return address;
    }
	public void updateCurrentSong(Song song) {
		libraryView.updateCurrentSong(song);
	}
}
