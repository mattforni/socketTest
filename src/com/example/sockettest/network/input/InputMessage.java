package com.example.sockettest.network.input;

import static com.example.sockettest.utils.Logger.tag;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.message.Message;

public abstract class InputMessage extends Message {
    public abstract void receive(final Device device);

    protected char nextChar(final SocketChannel channel) {
        try {
            channel.read(BUFFER);
        } catch (IOException e) {
            Log.e(tag(this), "Unable to read input stream", e);
        }
        return BUFFER.getChar();
    }

    @Override
    public void publish(SocketChannel channel) {
        // TODO This is a placeholder until all messages are moved
    }

    @Override
    public String serialize() {
        // TODO This is a placeholder until all messages are moved
        return null;
    }
}
