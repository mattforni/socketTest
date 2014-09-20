package com.example.sockettest.network.output;

import java.nio.channels.SocketChannel;

public abstract class OutputMessage {
    public abstract void publish(final SocketChannel channel);
}
