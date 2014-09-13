package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;

import java.io.InputStream;
import java.util.List;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.example.sockettest.network.Deserializer;
import com.example.sockettest.network.Message.InputMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public class ReceiveLibrary extends InputMessage {
    private static final String IDENTIFIER = "ReceiveLibrary";

    @Override
    public String getIdentifier() { return IDENTIFIER; }

    @Override
    public void receive(final InputStream input, final Device device) {
        Log.i(tag(this), "CODE 0 RECIEVED");

        final List<Song> songs = Lists.newLinkedList();
        final StringBuilder data = new StringBuilder();
        char currentChar = nextChar(input);
        while(currentChar != '\0') {
            if(currentChar == '\n') {
                final JsonElement json = JSON.parse(data.toString());
                songs.add(Deserializer.parseSong(json));

                // Clear the string buffer.
                data.setLength(0);
            } else {
                data.append(currentChar);
            }
            currentChar = nextChar(input);
        }

        device.updateLibrary(ImmutableList.copyOf(songs));
    }
}
