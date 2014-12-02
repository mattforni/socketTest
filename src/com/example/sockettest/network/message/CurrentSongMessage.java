package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.google.gson.JsonObject;

public class CurrentSongMessage extends Message {
    public static final int CODE = 4;

    private final Song currentSong;

    public CurrentSongMessage(final Song currentSong) {
        this.currentSong = currentSong;
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            final String message = serialize();
            channel.write(ByteBuffer.wrap(message.getBytes()));
            Log.i(tag(this), format("Published current song '%s'", currentSong.toString()));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }

    @Override
    public final void receive(final Device device) {
        if (currentSong != null) {
            device.updateCurrentSong(currentSong);
            Log.i(tag(this), format("Received current song '%s'", currentSong.toString()));
        } else {
            Log.e(tag(this), "Tried to set null current song");
        }
    }

    public final String serialize() {
		JsonObject jsonObject = Song.serialize(currentSong);
		jsonObject.addProperty(CODE_KEY,CODE);
		return jsonObject.toString();
    }

    public final static CurrentSongMessage deserialize(final JsonObject data) {
    	try {
            return new CurrentSongMessage(Song.deserialize(data));
        } catch (IllegalStateException e) {
            Log.w(tag(CurrentSongMessage.class), format("Unable to parse current song data"));
            return null;
        }
    }
}
