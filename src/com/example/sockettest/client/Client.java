package com.example.sockettest.client;

import static com.example.sockettest.utils.Logger.tag;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Source;
import com.example.sockettest.network.Message;
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
    public void onCreate(Bundle savedInstanceState) {
        try {
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
        } catch (IOException e) {
            Log.e(tag(this), "Unable to initialize client", e);
            System.exit(1);
        }
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
    public boolean next() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void publishMessage(Message message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void receiveMessage(Message message) {
        // TODO Auto-generated method stub
    }
}
