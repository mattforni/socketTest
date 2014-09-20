package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;
import android.util.Log;

import com.example.sockettest.Device;

public class ReceiveClientId extends InputMessage {
    private final String clientId;

    public ReceiveClientId(final String clientId) {
        this.clientId = clientId;
    }

    @Override
    public void receive(final Device device) {
        if (clientId != null) {
            device.setId(clientId);
            Log.i(tag(this), format("Client ID received %s", clientId));
        } else {
            Log.e(tag(this), "Tried to set null client ID");
        }
    }
}
