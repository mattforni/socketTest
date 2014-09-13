package com.example.sockettest.network;

import com.example.sockettest.music.Song;
import com.google.gson.JsonElement;

public abstract class Deserializer {
    public static final Song parseSong(final JsonElement serializedSong) {
        return Song.parse(serializedSong.getAsJsonObject());
    }
}
