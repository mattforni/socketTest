package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.input.Deserializer;
import com.example.sockettest.network.input.ReceiveCurrentSong;
import com.example.sockettest.network.input.ReceiveLibrary;
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
            case 1:
                return ClientIdMessage.deserialize(jsonObject);
            case 2:
            	return new ReceiveLibrary(Deserializer.parseLibrary(jsonObject));
            case 4:
                return new ReceiveCurrentSong(Deserializer.parseSong(jsonObject));
            default:
                Log.e(tag(Message.class), format("Unrecognized message code received: %d", code));
                return null;
        }
    }
}
