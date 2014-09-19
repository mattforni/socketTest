package com.example.sockettest.network;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.input.ReceiveClientId;
import com.example.sockettest.network.input.ReceiveLibrary;
import com.google.gson.JsonParser;

public abstract class Message {
    public abstract String getIdentifier();

    protected void handleException(final IOException e) {
        Log.e(getIdentifier(), "Unable to handle message", e);
    }

    public static abstract class InputMessage extends Message {
        protected static final byte[] BUFFER = new byte[1];
        protected static final JsonParser JSON = new JsonParser();

        public abstract void receive(final InputStream input, final Device device);

        protected char nextChar(final InputStream input) {
            try {
                input.read(BUFFER);
            } catch (IOException e) {
                Log.e(getIdentifier(), "Unable to read input stream", e);
            }
            return (char) BUFFER[0];
        }

        public static final InputMessage getMessage(final byte code) throws UnknownMessage {
            switch(code) {
                case 1:
                    return new ReceiveClientId();
                case 2:
                    return new ReceiveLibrary();
                default:
                    throw new UnknownMessage(code);
            }
        }
    }

    public static abstract class OutputMessage extends Message {
        public abstract void publish(final OutputStream output);
    }

    @SuppressWarnings("serial")
    public static class UnknownMessage extends RuntimeException {
        final private byte code;

        public UnknownMessage(final byte code) {
            super(format("UnknownMessage received for status code: %d", code));
            this.code = code;
        }

        public final boolean isReset() {
            if (code == 0) { return true; }
            return false;
        }
    }
}
