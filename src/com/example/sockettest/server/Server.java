package com.example.sockettest.server;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Random;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.SongManager.UnknownSongException;
import com.example.sockettest.network.Message;
import com.example.sockettest.ui.LibraryView;

public class Server extends Device {
    public static final String ADDRESS_KEY = "ADDRESS";
    public static final String PORT_KEY = "PORT";

    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 8080;
    private static final String ID = "SERVER";
    private static final Random RANDOM = new Random();

    private final ClientManager clientManager;
    private final MediaPlayer player;

    public Server() {
        super(ID);
        this.clientManager = new ClientManager(this);
        this.player = initializePlayer();
    }

    @Override
    public boolean enqueueSong(final int position, final boolean fromSearch) {
        boolean enqueued = false;
        try {
            songManager.enqueue(getSong(position, fromSearch));
            // TODO update playlist view
            // TODO notify clients of the update
            enqueued = true;
            Log.w(tag(this), format("Enqueued song: %d", position));
        } catch (UnknownSongException e) {
            Log.w(tag(this), format("Unable to enqueue song: %d", position));
        }
        return enqueued;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_view);
        initializeTabs();

        final Intent intent = getIntent();
        String address = intent.getStringExtra(ADDRESS_KEY);
        if (address == null) { address = DEFAULT_ADDRESS; }
        final int port = intent.getIntExtra(PORT_KEY, DEFAULT_PORT);
        try {
            clientManager.start(address, port);
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to initialize ClientManager on %s:%d", address, port));
        }

        this.libraryView = new LibraryView(this, true);
        this.libraryView.updateLibrary(songManager.getAllSongs());
    }

    @Override
    public void onTabChanged(final String tabId) {
        int pageNumber = 0;
        if(tabId.equals("tab1")){
            pageNumber = 0;
        } else if(tabId.equals("tab2")){
            pageNumber = 1;
        } else{
            pageNumber = 2;
        }
    }

    // TODO need to support playlists and streaming as well
    public final boolean next() {
        return playSong(getNext(), false);
    }

    // TODO need to support streaming
    public final boolean pause() {
        player.pause();
        playing = false;
        Log.i(tag(this), "Player is paused");
        return true;
    }

    // TODO need to support streaming
    public final boolean play() {
        if (currentIndex < 0) {
            return next();
        }
        player.start();
        playing = true;
        Log.i(tag(this), "Player is playing");
        return true;
    }

    public final boolean play(final int index, final boolean fromSearch) {
        return playSong(index, fromSearch);
    }

    public final boolean previous() {
        // TODO implement functionality to play previous song
        return false;
    }

    @Override
    public void publishMessage(final Message message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void receiveMessage(final Message message) {
        // TODO Auto-generated method stub
        
    }

    private int getNext() {
        if (songManager.isEmpty()) { return -1; }
        if (nextIndex > -1) { return nextIndex; }
        final int numSongs = songManager.numSongs();
        if (shuffle) {
            nextIndex = RANDOM.nextInt(numSongs);
        } else {
            nextIndex = (currentIndex + 1) >= numSongs ? 0 : currentIndex + 1;
        }
        return nextIndex;
    }

    private MediaPlayer initializePlayer() {
        final MediaPlayer player = new MediaPlayer();
        player.reset();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) { next(); }
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

    // TODO need to support streaming
    private boolean playSong(final int index, final boolean fromSearch) {
        // If the provided index is current index nothing should be done
        if (index == currentIndex) { return false; }
        try {
            final Song song = getSong(index, fromSearch);
            libraryView.updateCurrentSong(song);
            if(song.isLocal(this)) {
                player.reset();
                player.setDataSource(song.getPath());
                player.prepare();
                player.start();
                nextIndex = -1;
                Log.i(tag(this), format("Playing: %s", song.getPath()));
                return true;
            }
        } catch (UnknownSongException e) {
            Log.e(tag(this),"Unknown song selected for playback", e);
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            Log.e(tag(this),"Unexpected error during media playback", e);
        }
        return false;
    }
}
