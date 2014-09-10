package com.example.sockettest.server;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Server extends Device {
    public static final String ADDRESS_KEY = "ADDRESS";
    public static final String PORT_KEY = "PORT";

    private static final List<Map<String, String>> FAKE_SONGS = Lists.newArrayList();

    private boolean playing, searching, shuffle, streaming;
    private final MediaPlayer player;

    public Server() {
        this.playing = false;
        this.searching = false;
        this.shuffle = false;
        this.streaming = false;
        this.player = initializePlayer();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO change this name to client_view
        setContentView(R.layout.host_view);
        initializeTabs();
    }

    @Override
    public void onTabChanged(String tabId) {
        // TODO Auto-generated method stub
    }

    public final List<Map<String, String>> getLibrary() {
        return FAKE_SONGS;
    }

    public final boolean isPlaying() { return playing; }
    public final boolean isSearching() { return searching; }

    public synchronized final void next() {
        if(streaming) {
            streamSinkThread.pause();
            streamSinkThread.flush();
            streaming = false;
        }
        playSong(getNextSong());
    }

    public synchronized final void pause() {
        if (streaming) {
            Log.w("HOST","STREAMING");
            streamSinkThread.pause();
        } else {
            Log.w("HOST","NOT STREAMING");
            player.pause();
        }
        playing = false;
    }

    public synchronized final void play() {
        if (streaming) {
            Log.w("HOST","STREAMING");
            streamSinkThread.play();
        } else {
            Log.w("HOST","NOT STREAMING");
            player.start();
        }
        playing = true;
    }

    public synchronized final void previous() {
        // TODO implement functionality to play previous song
    }

    public final ImmutableList<Map<String, String>> search(final String query) {
        List<Map<String, String>> found = Lists.newLinkedList();
        for(final Map<String, String> song : getLibrary()) {
            String lowerTitle = song.get("title").toLowerCase(Locale.ENGLISH);
            String lowerArtist = song.get("artist").toLowerCase(Locale.ENGLISH);
            String lowerQuery = query.toLowerCase(Locale.ENGLISH);
            if(lowerTitle.contains(lowerQuery) || lowerArtist.contains(lowerQuery)) {
                found.add(song);
            }
        }
        return ImmutableList.copyOf(found);
    }

    private MediaPlayer initializePlayer() {
        MediaPlayer player = new MediaPlayer();
        player.reset();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                playSong(getNextSong());
            }
        });
        return player;
    }

    private void initializeTabs() {
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
