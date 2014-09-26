package com.example.sockettest.network.output;

import java.nio.channels.SocketChannel;

import com.example.sockettest.Device;
import com.example.sockettest.network.message.Message;

public abstract class OutputMessage extends Message {
    public abstract void publish(final SocketChannel channel);

    @Override
    public void receive(Device device) {
        // TODO This is a placeholder until all messages are moved
    }

    @Override
    public String serialize() {
        // TODO This is a placeholder until all messages are moved
        return null;
    }
}
