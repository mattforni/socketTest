package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import android.util.Log;

import com.example.sockettest.JsonSongDeserializer;
import com.example.sockettest.network.Message.InputMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ReceiveLibrary extends InputMessage {
    private static final byte[] BUFFER = new byte[1];
    private static final String IDENTIFIER = "ReceiveLibrary";
    private static final JsonSongDeserializer SONG_DESERIALIZER = new JsonSongDeserializer();

    @Override
    public String getIdentifier() { return IDENTIFIER; }

    @Override
    public void receive(final InputStream input) {
        // Receive music from the client
        Log.i(tag(this), "CODE 0 RECIEVED");
        char current = nextChar(input);
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

            current = nextChar(input);
        }

        // TODO create a good log message here
    }

    private char nextChar(final InputStream input) {
        try {
            input.read(BUFFER);
        } catch (IOException e) {
            handleException(e);
        }
        return (char) BUFFER[0];
    }
}
