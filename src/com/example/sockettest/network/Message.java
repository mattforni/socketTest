package com.example.sockettest.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public abstract class Message {
    public abstract String getIdentifier();

    protected void handleException(final IOException e) {
        Log.e(getIdentifier(), "Unable to handle message", e);
    }

    public static abstract class InputMessage extends Message {
        public abstract void receive(final InputStream input);
    }

    public static abstract class OutputMessage extends Message {
        public abstract void publish(final OutputStream output);
    }
}
