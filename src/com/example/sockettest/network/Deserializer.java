package com.example.sockettest.network;

import static java.lang.String.format;
import android.util.Log;

import com.example.sockettest.music.Song;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Deserializer {
    public static final String parseId(final JsonElement serializedId) {
        try {
            final JsonObject jsonObject = serializedId.getAsJsonObject();
            return jsonObject.get("ID").getAsString();
        } catch (IllegalStateException e) {
            Log.w("Deserializer", format("Unable to parse id from %s", serializedId));
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
