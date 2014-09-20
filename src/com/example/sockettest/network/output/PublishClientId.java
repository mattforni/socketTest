package com.example.sockettest.network.output;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.google.gson.JsonElement;

public class PublishClientId extends OutputMessage {
    public static final int CODE = 1;

    private final JsonElement message;

    public PublishClientId(final String clientIdString) {
        this.message = Serializer.publishClientId(clientIdString);
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            channel.write(ByteBuffer.wrap(message.toString().getBytes()));
            Log.i(tag(this), format("Publishing client id %s", message));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }
}
