package com.example.sockettest.client;

import static com.example.sockettest.utils.Logger.tag;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.example.sockettest.music.Source;
import com.example.sockettest.network.NetworkLayer;
import com.example.sockettest.network.message.ClientIdMessage;
import com.example.sockettest.network.message.PlaylistMessage;
import com.example.sockettest.network.message.LibraryMessage;
import com.example.sockettest.ui.LibraryView;
import com.example.sockettest.ui.PlaylistView;
import com.example.sockettest.ui.SettingsView;

public class Client extends Device {
    private NetworkLayer network;

    public Client() {
        super(null);
    }

    @Override
    public boolean enqueueSong(final Source source, final int position) {
    	// Wrap single song in list and pass to PublishPlayList for uniformity
    	List<Song> wrappedSong = new LinkedList<Song>();
    	wrappedSong.add(Source.LIBRARY.get(position));
    	network.publishMessage(new PlaylistMessage(wrappedSong));
        return false;
    }

    @SuppressLint("NewApi")
	@Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeTabs();
        
        final Intent intent = getIntent();
        this.address = intent.getStringExtra(ADDRESS_KEY);
        this.port = intent.getIntExtra(PORT_KEY, 0);
        this.network = new NetworkLayer(this, address, port);
        this.libraryView = new LibraryView(this);
        this.playlistView = new PlaylistView(this);
        this.settingsView = new SettingsView(this);

        Log.i(tag(this), "Client successfully initialized");
    }

    @Override
    public final void onDestroy() {
        network.disconnect();
        super.onDestroy();
    }
    
    public final void stream(Song song) {
    	// TODO send audio data
    }

    // TODO need to support streaming
    public final boolean pause() {
        // TODO send message to Server to pause across network
        return true;
    }

    // TODO need to support streaming
    public final boolean play() {
        // TODO send message to Server to play across network
        return true;
    }

    public final boolean play(final Source source, final Song song) {
        // TODO send message to Server to play across network
        return true;
    }

    public final boolean previous() {
        // TODO send message to Server to previous across network
        return false;
    }

    @Override
    public boolean next() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public final void receiveClientId(final String id, final boolean reconnect) {
        final String oldId = this.id;
        this.id = id;
        if (oldId == null) {
        	songManager.loadLibrary();
            libraryView.updateLibrary(songManager.getAllSongs());
            network.publishMessage(new LibraryMessage(songManager.getAllSongs()));
        } else {
            // Alert the server that client ID has already been set
            network.publishMessage(new ClientIdMessage(this.id, true));
        }
    }
    
    public final void updateLibrary(final List<Song> songs) {
        Source.LIBRARY.update(songs);
        libraryView.updateLibrary(songManager.getAllSongs());
    }
    
    public final void updatePlaylist(List<Song> playlist) {
    	Source.PLAYLIST.update(playlist);
    	if(songManager.getPlaylist() == null) {
    		Log.i(tag(this), "PLAYLIST IS NULL");
    	}
    	else {
    		playlistView.updatePlaylist(songManager.getPlaylist());
    	}
	}
}
