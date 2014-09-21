package com.example.sockettest.client;

import static com.example.sockettest.utils.Logger.tag;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Source;
import com.example.sockettest.network.NetworkLayer;
import com.example.sockettest.ui.LibraryView;

public class Client extends Device {
    private NetworkLayer network;

    public Client() {
        super(null);
    }

    @Override
    public boolean enqueueSong(final Source source, final int position) {
        // TODO Send song to Server across network
        return false;
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_view);
        initializeTabs();

        final Intent intent = getIntent();
        final String address = intent.getStringExtra(ADDRESS_KEY);
        final int port = intent.getIntExtra(PORT_KEY, 0);
        this.network = new NetworkLayer(this, address, port);

        songManager.loadLibrary();
        this.libraryView = new LibraryView(this);
        this.libraryView.updateLibrary(songManager.getAllSongs());

        Log.i(tag(this), "Client successfully initialized");
    }

    @Override
    public final void onDestroy() {
        network.disconnect();
        super.onDestroy();
    }

    // TODO need to support streaming
    public final boolean pause() {
        // TODO send message to Server to pause accross network
        return true;
    }

    // TODO need to support streaming
    public final boolean play() {
        
        // TODO send message to Server to play accross network
        return true;
    }

    public final boolean play(final Source source, final int index) {
        // TODO send message to Server to play accross network
        return true;
    }

    public final boolean previous() {
        // TODO send message to Server to previous across network
        return false;
    }

    @Override
    public final void setId(final String id) {
        final String oldId = this.id;
        this.id = id;
        if (oldId == null) {
            // TODO load the library and update the view
        } else {
            // TODO send update to server with old client id
        }
    }

    @Override
    public boolean next() {
        // TODO Auto-generated method stub
        return false;
    }
}
