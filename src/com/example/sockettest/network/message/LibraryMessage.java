package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LibraryMessage extends Message {
    public static final int CODE = 2;

    private static final String LIBRARY_KEY = "LIBRARY";

    private final List<Song> library;

    public LibraryMessage(final List<Song> library) {
        this.library = library;
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            final String library = serialize();
            channel.write(ByteBuffer.wrap(library.getBytes()));
            Log.i(tag(this), format("Published library"));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }

    @Override
    public final void receive(final Device device) {
        if (library != null) {
            device.updateLibrary(library);
            Log.i(tag(this), format("Received library"));
        } else {
            Log.e(tag(this), "Tried to update library");
        }
    }

    @Override
    public final String serialize() {
        JsonObject serializedPlaylist = new JsonObject();
        serializedPlaylist.addProperty(CODE_KEY, CODE);

        final JsonArray serializedSongs = new JsonArray();
        for(Song song : library) {
            serializedSongs.add(Song.serialize(song));
        }

        serializedPlaylist.add(LIBRARY_KEY, serializedSongs);
        return serializedPlaylist.toString();
    }

    public final static LibraryMessage deserialize(final JsonObject data) {
        final List<Song> library = new LinkedList<Song>();
        JsonArray serializedSongs = data.get(LIBRARY_KEY).getAsJsonArray();
        for(int i = 0; i < serializedSongs.size(); i++) {
            library.add(Song.deserialize(serializedSongs.get(i).getAsJsonObject()));
        }
        try {
            return new LibraryMessage(library);
        } catch (IllegalStateException e) {
            Log.w(tag(LibraryMessage.class), format("Unable to parse playlist data"));
            return null;
        }
    }
}
