package com.example.sockettest.actions;

import static com.example.sockettest.utils.Logger.tag;

import java.io.IOException;
import java.util.Map;

import android.util.Log;

import com.example.sockettest.JsonSongDeserializer;
import com.example.sockettest.network.NetworkLayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class InitializeClient extends Action {
    private static final byte[] BUFFER = new byte[1];
    private static final String IDENTIFIER = "InitializeClient";
    private static final JsonSongDeserializer SONG_DESERIALIZER = new JsonSongDeserializer();

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void perform(final NetworkLayer network) {
        if(network.isInitialized()) {
            network.disconnect();
            return;
        }

        // Receive music from the client
        Log.i(tag(this), "CODE 0 RECIEVED");
        char current = nextChar(network);
        final StringBuilder string = new StringBuilder();

        while(current != '\0') {
            if(current == '\n') {
                // TODO figure out what to do with the song
                JsonElement json = new JsonParser().parse(string.toString());
                Map<String,String> song = SONG_DESERIALIZER.deserialize(json, null, null);
//                localMusicList.add(song);

                // Clear the string buffer.
                string.setLength(0);
            } else {
                string.append(current);
            }

            current = nextChar(network);
        }

//        Message msg = Message.obtain();
//        updateUIHandler.sendMessage(msg);
        // TODO this isn't really the network that is initialized ...
        network.setInitialized(true);
        // TODO create a good log message here
    }

    private char nextChar(final NetworkLayer network) {
        try {
            network.input().read(BUFFER);
        } catch (IOException e) {
            handleException(e);
        }
        return (char) BUFFER[0];
    }
}
