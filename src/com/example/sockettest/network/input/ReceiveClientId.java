package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.Deserializer;
import com.example.sockettest.network.Message.InputMessage;

public class ReceiveClientId extends InputMessage {
    private static final String IDENTIFIER = "ReceiveClientId";

    @Override
    public String getIdentifier() { return IDENTIFIER; }

    @Override
    public void receive(final SocketChannel channel, final Device device) {
        // TODO update this to be NIO friendly
        Log.w(tag(this), "Receiving clientId");

        final StringBuilder data = new StringBuilder();
        char currentChar = nextChar(channel);
        while(currentChar != '\0') {
            data.append(currentChar);
            currentChar = nextChar(channel);
        }

        final String clientId = Deserializer.parseId(JSON.parse(data.toString()));
        if (clientId != null) {
            device.setId(clientId);
            Log.w(tag(this), format("ClientId received: %s", clientId));
        }
    }
}
