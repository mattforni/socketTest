package com.example.sockettest.server;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.Source;
import com.example.sockettest.music.Source.UnknownSongException;
import com.example.sockettest.network.message.StreamMessage;
import com.example.sockettest.ui.LibraryView;
import com.example.sockettest.ui.PlaylistView;
import com.example.sockettest.ui.SettingsView;

public class Server extends Device {
    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 8080;
    private static final String ID = "SERVER";
    private static final int ADD_PLAYED_SONG_TIMER = 5000;
    private static final int WAIT_TIMER = 1000;
    
    private ClientManager clientManager;
    private final MediaPlayer player;
    private final Handler playedSongsHandler;
    private Runnable addPlayedSongsRunnable;

    public Server() {
        super(ID);
        this.isServer = true;
        this.player = initializePlayer();
        this.playedSongsHandler = new Handler();
    }

    @Override
    public boolean enqueueSong(final Source source, final int position) {
        boolean enqueued = false;
        try {
            songManager.enqueue(source.get(position));
            playlistView.updatePlaylist(songManager.getPlaylist());
            clientManager.publishPlaylist(songManager.getPlaylist());
            enqueued = true;
            Log.w(tag(this), format("Enqueued song: %d", position));
        } catch (UnknownSongException e) {
            Log.w(tag(this), format("Unable to enqueue song: %d", position));
        }
        return enqueued;
    }

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeTabs();

        final Intent intent = getIntent();
        this.address = intent.getStringExtra(ADDRESS_KEY);
        if (address == null) { address = DEFAULT_ADDRESS; }
        this.port = intent.getIntExtra(PORT_KEY, DEFAULT_PORT);
        clientManager = new ClientManager(this, address, port);
        clientManager.start();

        songManager.loadLibrary();
        this.libraryView = new LibraryView(this);
        this.libraryView.updateLibrary(songManager.getAllSongs());
        this.playlistView = new PlaylistView(this);
        this.settingsView = new SettingsView(this);

        Log.i(tag(this), "Server successfully initialized");
    }

    @Override
    public final boolean next() {
        return play();
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
            final Song song = songManager.getNext();
            if (song != null) { playing = playSong(Source.LIBRARY, song); }
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

    public final boolean play(final Source source, final Song song) {
        if (songManager.isPlaying(source, song)) { return false; }
        final boolean playing = playSong(source, song);
        if (playing) {
            libraryView.showPauseButton();
        }
        return playing;
    }

    @Override
    public final boolean previous() {
        return play(Source.LIBRARY, songManager.getPrevious());
    }

    @Override
    public final void receiveClientId(final String id, final boolean reconnect) {
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
    private boolean playSong(final Source source, final Song song) {
        // TODO If the provided index is current index nothing should be done
    	
        try {
            libraryView.updateCurrentSong(song);
            clientManager.publishCurrentSong(song);
            songManager.setCurrentSong(song);
            preparePlayedSongsHandler(song);
            
            if(song.isLocal(this)) {
                player.reset();
                player.setDataSource(song.getPath());
                player.prepare();
                player.start();
                Log.i(tag(this), format("Playing: %s", song.getPath()));
                return true;
            } else {
            	clientManager.publishMessage(song.getOwner(), new StreamMessage(song));
            	return true;
            }
        } catch (UnknownSongException e) {
            Log.e(tag(this),"Unknown song selected for playback", e);
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            Log.e(tag(this),"Unexpected error during media playback", e);
        }
        return false;
    }

    public final void stream(Song song) {
    	// TODO format and play audio data
    }
    
    public Song getCurrentSong() {
    	return songManager.getCurrentSong();
    }
    
    public List<Song> getPlaylist() {
    	return songManager.getPlaylist();
    }
    
    public final void updateLibrary(final List<Song> songs) {
        Source.LIBRARY.append(songs);
        libraryView.updateLibrary(songManager.getAllSongs());
        // TODO CRASHES ONLY BECAUSE LENGTH OF LIBRARY IS TOO LONG AT THE MOMENT
        clientManager.publishLibrary(songManager.getAllSongs());
    }
    
    public final void updatePlaylist(List<Song> playlist) {
    	songManager.enqueue(playlist);
        playlistView.updatePlaylist(songManager.getPlaylist());
        clientManager.publishPlaylist(songManager.getPlaylist());
	}
    
	private void preparePlayedSongsHandler(final Song song) {
		playedSongsHandler.removeCallbacks(addPlayedSongsRunnable);
        addPlayedSongsRunnable = new Runnable() {
			@Override
			public void run() {
				while(player.getCurrentPosition() < ADD_PLAYED_SONG_TIMER) {
					try {
						Thread.sleep(WAIT_TIMER);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				songManager.pushPrevious(song);
			}
        };
        playedSongsHandler.postDelayed(addPlayedSongsRunnable, ADD_PLAYED_SONG_TIMER);
	}
}
