package com.example.sockettest.network;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.input.ReceiveClientId;
import com.example.sockettest.network.input.ReceiveLibrary;
import com.google.gson.JsonParser;

public abstract class Message {
    protected static final ByteBuffer END_BUFFER = ByteBuffer.allocate(2).putChar('\0');

    public abstract String getIdentifier();

    protected void handleException(final IOException e) {
        Log.e(getIdentifier(), "Unable to handle message", e);
    }

    public static abstract class InputMessage extends Message {
        protected static final ByteBuffer BUFFER = ByteBuffer.allocate(1);
        protected static final JsonParser JSON = new JsonParser();

        public abstract void receive(final SocketChannel channel, final Device device);

        protected char nextChar(final SocketChannel channel) {
            try {
                channel.read(BUFFER);
            } catch (IOException e) {
                Log.e(getIdentifier(), "Unable to read input stream", e);
            }
            return BUFFER.getChar();
        }

        public static final InputMessage getMessage(final int code) throws UnknownMessage {
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
        public abstract void publish(final SocketChannel channel);
    }

    @SuppressWarnings("serial")
    public static class UnknownMessage extends RuntimeException {
        final private int code;

        public UnknownMessage(final int code) {
            super(format("UnknownMessage received for status code: %d", code));
            this.code = code;
        }

        public final boolean isReset() {
            if (code == 0) { return true; }
            return false;
        }
    }
}
