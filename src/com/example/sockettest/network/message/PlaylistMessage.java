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

public class PlaylistMessage extends Message {
    public static final int CODE = 3;

    private static final String PLAYLIST_KEY = "PLAYLIST";

    private final List<Song> playlist;

    public PlaylistMessage(final List<Song> playlist) {
        this.playlist = playlist;
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            final String message = serialize();
            channel.write(ByteBuffer.wrap(message.getBytes()));
            Log.i(tag(this), format("Published client id %s", message));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }

    @Override
    public final void receive(final Device device) {
        if (playlist != null) {
            device.updatePlaylist(playlist);
            Log.i(tag(this), format("Updated playlist"));
        } else {
            Log.e(tag(this), "Tried to update playlist");
        }
    }

    public final String serialize() {
    	JsonObject serializedPlaylist = new JsonObject();
		serializedPlaylist.addProperty(CODE_KEY, CODE);
		
		final JsonArray serializedSongs = new JsonArray();
		for(Song song : playlist) {
			serializedSongs.add(Song.serialize(song));
		}
		
		serializedPlaylist.add(PLAYLIST_KEY, serializedSongs);
		return serializedPlaylist.toString();
    }

    public final static PlaylistMessage deserialize(final JsonObject data) {
    	final List<Song> playlist = new LinkedList<Song>();
		JsonArray serializedSongs = data.get(PLAYLIST_KEY).getAsJsonArray();
		for(int i = 0; i < serializedSongs.size(); i++) {
			playlist.add(Song.deserialize(serializedSongs.get(i).getAsJsonObject()));
		}
    	try {
            return new PlaylistMessage(playlist);
        } catch (IllegalStateException e) {
            Log.w(tag(PlaylistMessage.class), format("Unable to parse playlist data"));
            return null;
        }
    }
}
