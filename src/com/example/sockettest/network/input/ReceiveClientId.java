package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.InputStream;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.JsonIdDeserializer;
import com.example.sockettest.network.Message.InputMessage;
import com.google.gson.JsonElement;

public class ReceiveClientId extends InputMessage {
    private static final String IDENTIFIER = "ReceiveClientId";

    @Override
    public String getIdentifier() { return IDENTIFIER; }

    @Override
    public void receive(final InputStream input, final Device device) {
        Log.w(tag(this), "Receiving clientId");

        final StringBuilder data = new StringBuilder();
        char currentChar = nextChar(input);
        while(currentChar != '\0') {
            data.append(currentChar);
            currentChar = nextChar(input);
        }

        JsonElement jsonElement = JSON.parse(data.toString());
        JsonIdDeserializer deserializerId = new JsonIdDeserializer();
        String clientId = deserializerId.deserialize(jsonElement, null, null);
        device.setId(clientId);

        Log.w(tag(this), format("ClientId received: ", clientId));
    }
}
