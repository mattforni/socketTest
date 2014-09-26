package com.example.sockettest.network.output;

import static com.example.sockettest.network.message.Message.CODE_KEY;

import java.util.List;

import com.example.sockettest.music.Song;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Serializer {
    public static final JsonElement publishCurrentSong(final Song currentSong) {
    	JsonObject serializedSong = serializeSong(currentSong);
    	serializedSong.addProperty(CODE_KEY, PublishCurrentSong.CODE);
    	return serializedSong;
    }

	public static JsonElement publishLibrary(List<Song> library) {
		// TODO is this right?
		final JsonArray jsonArray = new JsonArray();
		for(Song song : library) {
			jsonArray.add(serializeSong(song));
		}
		return jsonArray;
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
