package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class Message {
    public static final String CODE_KEY = "CODE";

    protected static final ByteBuffer BUFFER = ByteBuffer.allocate(1);
    protected static final JsonParser JSON = new JsonParser();

    public abstract void publish(final SocketChannel channel);
    public abstract void receive(final Device device);
    public abstract String serialize();

    protected char nextChar(final SocketChannel channel) {
        try {
            channel.read(BUFFER);
        } catch (IOException e) {
            Log.e(tag(this), "Unable to read input stream", e);
        }
        return BUFFER.getChar();
    }

    public static final Message getMessage(final String data) {
        final JsonObject jsonObject = JSON.parse(data).getAsJsonObject();
        final int code = jsonObject.get(CODE_KEY).getAsInt();
        switch(code) {
            case ClientIdMessage.CODE:
                return ClientIdMessage.deserialize(jsonObject);
            case LibraryMessage.CODE:
            	return LibraryMessage.deserialize(jsonObject);
            case PlaylistMessage.CODE:
                return PlaylistMessage.deserialize(jsonObject);
            case CurrentSongMessage.CODE:
                return CurrentSongMessage.deserialize(jsonObject);
            default:
                Log.e(tag(Message.class), format("Unrecognized message code received: %d", code));
                return null;
        }
    }
}
