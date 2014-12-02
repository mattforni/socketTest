package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.google.gson.JsonObject;

public class ClientIdMessage extends Message {
    public static final int CODE = 1;

    private static final String ID_KEY = "ID";
    private static final String RECONNECT_KEY = "RECONNECT";

    private final String id;
    private final boolean reconnect;

    public ClientIdMessage(final String id, final boolean reconnect) {
        this.id = id;
        this.reconnect = reconnect;
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            final String message = serialize();
            channel.write(ByteBuffer.wrap(message.getBytes()));
            Log.i(tag(this), format("Published client ID '%s'", message));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }

    @Override
    public final void receive(final Device device) {
        if (id != null) {
            device.receiveClientId(id, reconnect);
            Log.i(tag(this), format("Received client ID '%s'", id));
        } else {
            Log.e(tag(this), "Tried to set null client ID");
        }
    }

    @Override
    public final String serialize() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CODE_KEY, CODE);
        jsonObject.addProperty(ID_KEY, id);
        jsonObject.addProperty(RECONNECT_KEY, reconnect);
        return jsonObject.toString();
    }

    public static final ClientIdMessage deserialize(final JsonObject data) {
        try {
            return new ClientIdMessage(
                    data.get(ID_KEY).getAsString(),
                    data.get(RECONNECT_KEY).getAsBoolean());
        } catch (IllegalStateException e) {
            Log.w(tag(ClientIdMessage.class), format("Unable to parse client id from %s", data));
            return null;
        }
    }
}
