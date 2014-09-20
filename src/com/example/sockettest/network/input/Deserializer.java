package com.example.sockettest.network.input;

import static com.example.sockettest.network.output.Serializer.ID_KEY;
import static java.lang.String.format;
import android.util.Log;

import com.example.sockettest.music.Song;
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
}
