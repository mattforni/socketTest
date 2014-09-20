package com.example.sockettest.network.output;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.network.Message.OutputMessage;
import com.example.sockettest.network.Serializer;
import com.google.gson.JsonElement;

public class PublishClientId extends OutputMessage {
    private static final ByteBuffer CODE_BUFFER = ByteBuffer.allocate(4).putInt(1);
    private static final String IDENTIFIER = "PublishClientId";

    private final JsonElement clientId;

    public PublishClientId(final String clientIdString) {
        this.clientId = Serializer.serializeId(clientIdString);
    }

    @Override
    public final String getIdentifier() { return IDENTIFIER; }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            channel.write(CODE_BUFFER);
            channel.write(ByteBuffer.wrap(clientId.toString().getBytes()));
            channel.write(END_BUFFER);
            Log.i(tag(this), format("Publishing client id %s", clientId));
        } catch (IOException e) {
            handleException(e);
        }
    }
}
