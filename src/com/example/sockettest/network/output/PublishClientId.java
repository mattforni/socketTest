package com.example.sockettest.network.output;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

import com.example.sockettest.JsonIdSerializer;
import com.example.sockettest.network.Message.OutputMessage;
import com.google.gson.JsonElement;

public class PublishClientId extends OutputMessage {
    private static final JsonIdSerializer ID_SERIALIZER = new JsonIdSerializer();
    private static final String IDENTIFIER = "SendClientId";

    private final JsonElement clientId;

    public PublishClientId(final String clientIdString) {
        this.clientId = ID_SERIALIZER.serialize(clientIdString, null, null);
    }

    @Override
    public final String getIdentifier() { return IDENTIFIER; }

    @Override
    public final void publish(final OutputStream output) {
        try {
            output.write(0);
            output.write(clientId.toString().getBytes());
            output.write('\0');
            Log.i(tag(this), format("Client id sent to %s", clientId));
        } catch (IOException e) {
            handleException(e);
        }
    }
}
