package com.example.sockettest.network.output;

import java.util.List;

import com.example.sockettest.music.Song;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Serializer {
    public static final String CODE_KEY = "CODE";
    public static final String ID_KEY = "ID";
    public static final String LIBRARY_KEY = "LIBRARY";
    public static final String PLAYLIST_KEY = "PLAYLIST";

    public static final JsonElement publishClientId(final String id) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CODE_KEY, PublishClientId.CODE);
        jsonObject.addProperty(ID_KEY, id);
        return jsonObject;
    }
    
    public static final JsonElement publishCurrentSong(final Song currentSong) {
    	JsonObject serializedSong = serializeSong(currentSong);
    	serializedSong.addProperty(CODE_KEY, PublishCurrentSong.CODE);
    	return serializedSong;
    }

	public static JsonElement publishLibrary(List<Song> library) {
		JsonObject serializedLibrary = new JsonObject();
		serializedLibrary.addProperty(CODE_KEY, PublishLibrary.CODE);
		
		final JsonArray serializedSongs = new JsonArray();
		for(Song song : library) {
			serializedSongs.add(serializeSong(song));
		}
		
		serializedLibrary.add(LIBRARY_KEY, serializedSongs);
		return serializedLibrary;
	}

	public static JsonElement publishPlaylist(List<Song> playlist) {
		JsonObject serializedPlaylist = new JsonObject();
		serializedPlaylist.addProperty(CODE_KEY, PublishPlaylist.CODE);
		
		final JsonArray serializedSongs = new JsonArray();
		for(Song song : playlist) {
			serializedSongs.add(serializeSong(song));
		}
		
		serializedPlaylist.add(PLAYLIST_KEY, serializedSongs);
		return serializedPlaylist;
	}
	
	private static final JsonObject serializeSong(final Song currentSong) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Song.TITLE_KEY, currentSong.getTitle());
        jsonObject.addProperty(Song.ARTIST_KEY, currentSong.getArtist());
        jsonObject.addProperty(Song.OWNER_KEY, currentSong.getOwner());
        jsonObject.addProperty(Song.PATH_KEY, currentSong.getPath());
        jsonObject.addProperty(Song.TYPE_KEY, currentSong.getFormat().toString());
        return jsonObject;
    }
}