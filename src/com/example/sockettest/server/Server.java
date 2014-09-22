package com.example.sockettest.server;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.Source;
import com.example.sockettest.music.Source.UnknownSongException;
import com.example.sockettest.ui.LibraryView;
import com.example.sockettest.ui.PlaylistView;

public class Server extends Device {
    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 8080;
    private static final String ID = "SERVER";

    private final MediaPlayer player;

    public Server() {
        super(ID);
        this.isServer = true;
        this.player = initializePlayer();
    }

    @Override
    public boolean enqueueSong(final Source source, final int position) {
        boolean enqueued = false;
        try {
            songManager.enqueue(source.get(position));
            playlistView.updatePlaylist(songManager.getPlaylist());
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
        new ClientManager(this, address, port).start();

        songManager.loadLibrary();
        this.libraryView = new LibraryView(this);
        this.libraryView.updateLibrary(songManager.getAllSongs());
        this.playlistView = new PlaylistView(this);

        Log.i(tag(this), "Server successfully initialized");
    }

    @Override
    public final boolean next() {
        // TODO implements to support types as well
        return false;
    }

    @Override
    public final boolean pause() {
        // TODO need to support streaming
        if (!player.isPlaying()) { return false; }
        player.pause();
        libraryView.showPlayButton();
        Log.i(tag(this), "Playback is paused");
        return true;
    }

    @Override
    public final boolean play() {
        // TODO need to support streaming
        if (player.isPlaying()) { return false; }
        boolean playing = false;
        if (songManager.current() == -1) {
            final int index = songManager.next();
            if (index > -1) { playing = playSong(Source.LIBRARY, index); }
        } else {
            player.start();
            playing = true;
            Log.i(tag(this), "Playback has resumed");
        }
        if (playing) {
            libraryView.showPauseButton();
        }
        return playing;
    }

    @Override
    public final boolean play(final Source source, final int index) {
        if (songManager.isPlaying(source, index)) { return false; }
        final boolean playing = playSong(source, index);
        if (playing) {
            libraryView.showPauseButton();
        }
        return playing;
    }

    @Override
    public final boolean previous() {
        // TODO implement functionality to play previous song
        return false;
    }

    @Override
    public final void receiveClientId(final String id) {
        // TODO will need a way to update reference from client to network
    }

    private MediaPlayer initializePlayer() {
        final MediaPlayer player = new MediaPlayer();
        player.reset();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) { next(); }
        });
        return player;
    }

    // TODO need to support streaming
    private boolean playSong(final Source source, final int index) {
        // TODO If the provided index is current index nothing should be done
        try {
            final Song song = getSong(source, index);
            libraryView.updateCurrentSong(song);
            if(song.isLocal(this)) {
                player.reset();
                player.setDataSource(song.getPath());
                player.prepare();
                player.start();
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
