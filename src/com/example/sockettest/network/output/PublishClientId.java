package com.example.sockettest.network.output;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

import com.example.sockettest.network.Message.OutputMessage;
import com.example.sockettest.network.Serializer;
import com.google.gson.JsonElement;

public class PublishClientId extends OutputMessage {
    private static final String IDENTIFIER = "PublishClientId";

    private final JsonElement clientId;

    public PublishClientId(final String clientIdString) {
        this.clientId = Serializer.serializeId(clientIdString);
    }

    @Override
    public final String getIdentifier() { return IDENTIFIER; }

    @Override
    public final void publish(final OutputStream output) {
        try {
            output.write(0);
            output.write(clientId.toString().getBytes());
            output.write('\0');
            Log.i(tag(this), format("Publishing client id %s", clientId));
        } catch (IOException e) {
            handleException(e);
        }
    }
}
