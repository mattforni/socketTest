package com.example.sockettest.network.input;

import static com.example.sockettest.network.output.Serializer.ID_KEY;
import static com.example.sockettest.network.output.Serializer.LIBRARY_KEY;
import static com.example.sockettest.network.output.Serializer.PLAYLIST_KEY;
import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.example.sockettest.music.Song;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Deserializer {
    public static final String clientId(final JsonObject data) {
        try {
            return data.get(ID_KEY).getAsString();
        } catch (IllegalStateException e) {
            Log.w("Deserializer", format("Unable to parse id from %s", data));
        }
        return null;
    }

    public static final Song parseSong(final JsonElement serializedSong) {
        try {
            return Song.parse(serializedSong.getAsJsonObject());
        } catch (IllegalStateException e) {
            Log.w("Deserializer", format("Unable to parse song from %s", serializedSong));
        }
        return null;
    }

	public static List<Song> parseLibrary(final JsonObject serializedLibrary) {
		final List<Song> library = new LinkedList<Song>();
		JsonArray serializedSongs = serializedLibrary.get(LIBRARY_KEY).getAsJsonArray();
		for(int i = 0; i < serializedSongs.size(); i++) {
			library.add(Song.parse(serializedSongs.get(i).getAsJsonObject()));
		}
		return library;
	}
	
	public static List<Song> parsePlaylist(final JsonObject serializedPlaylist) {
		final List<Song> playlist = new LinkedList<Song>();
		JsonArray serializedSongs = serializedPlaylist.get(PLAYLIST_KEY).getAsJsonArray();
		for(int i = 0; i < serializedSongs.size(); i++) {
			playlist.add(Song.parse(serializedSongs.get(i).getAsJsonObject()));
		}
		return playlist;
	}
}
