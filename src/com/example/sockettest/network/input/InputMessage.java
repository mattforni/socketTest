package com.example.sockettest.network.input;

import static com.example.sockettest.network.output.Serializer.CODE_KEY;
import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.output.PublishClientId;
import com.example.sockettest.network.output.PublishCurrentSong;
import com.example.sockettest.network.output.PublishLibrary;
import com.example.sockettest.network.output.PublishPlaylist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class InputMessage {
    protected static final ByteBuffer BUFFER = ByteBuffer.allocate(1);
    protected static final JsonParser JSON = new JsonParser();

    public abstract void receive(final Device device);

    protected char nextChar(final SocketChannel channel) {
        try {
            channel.read(BUFFER);
        } catch (IOException e) {
            Log.e(tag(this), "Unable to read input stream", e);
        }
        return BUFFER.getChar();
    }

    public static final InputMessage getMessage(final String data) {
        final JsonObject jsonObject = JSON.parse(data).getAsJsonObject();
        final int code = jsonObject.get(CODE_KEY).getAsInt();
        switch(code) {
            case PublishClientId.CODE:
                return new ReceiveClientId(Deserializer.clientId(jsonObject));
            case PublishLibrary.CODE:
            	return new ReceiveLibrary(Deserializer.parseLibrary(jsonObject));
            case PublishPlaylist.CODE:
            	return new ReceivePlaylist(Deserializer.parsePlaylist(jsonObject));
            case PublishCurrentSong.CODE:
                return new ReceiveCurrentSong(Deserializer.parseSong(jsonObject));
            default:
                Log.e("InputMessage", format("Unrecognized message code received: %d", code));
                return null;
        }
    }
}
