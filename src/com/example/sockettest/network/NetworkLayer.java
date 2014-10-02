package com.example.sockettest.network;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.message.Message;

public class NetworkLayer {
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(1024);

    private final Device device;

    private String address;
    private int port;
    private SocketChannel channel;
    private boolean shutdown;

    public NetworkLayer(final Device device, final SocketChannel channel) {
        this.device = device;
        this.channel = channel;
        this.shutdown = false;
        new Connection().start();
    }

    public NetworkLayer(final Device device, final String address, final int port) {
        this.device = device;
        this.address = address;
        this.port = port;
        this.shutdown = false;
        new Connection().start();
    }

    public final synchronized void disconnect() {
        try {
            channel.close();
            shutdown = true;
            Log.i(tag(this), "Client is now disconnected");
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to close I/O"), e);
        }
    }

    public final synchronized void publishMessage(final Message message) {
        new Thread() {
            @Override
            public synchronized void run() { message.publish(channel); }
        }.start();
    }

    public final synchronized void reset() {
        try {
            if (channel != null) {
                channel.close();
                channel = null;
            }
            new Connection().start();
        } catch (IOException e) {
            Log.e(tag(this), "Unable to reset connection", e);
            System.exit(1);
        }
    }

    private final synchronized void initializeChannel() {
        try {
            channel = SocketChannel.open();
            channel.socket().setSoTimeout(0);
            channel.socket().setKeepAlive(true);
            channel.connect(new InetSocketAddress(address, port));
            Log.i(tag(this), format("Channel connected to %s:%d", address, port));
        } catch (UnknownHostException e) {
            Log.e(tag(this), format("Could not find server at %s:%d", address, port), e);
            System.exit(1);
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to initialize channel on %s:%d", address, port), e);
            System.exit(1);
        }
    }

    private class Connection extends Thread {
        @Override
        public void run() {
            if (channel == null) { initializeChannel(); }
            new InputListener().start();
        }
    }

    private class InputListener extends Thread {
        @Override
        public void run() {
            boolean reset = false;
            while(!shutdown) {
                try {
                    final StringBuilder data = new StringBuilder();
                    BUFFER.clear();

                    while (channel.read(BUFFER) > 0) {
                        final int remaining = BUFFER.remaining();

                        BUFFER.flip();
                        for (int i = 0; i < BUFFER.limit(); i++) {
                            data.append((char)BUFFER.get());
                        }

                        // The entire message has been read
                        if (remaining > 0) { break; }
                        BUFFER.clear();
                    }

                    if (data.length() > 0) {
                        Log.i(tag(this), format("Received %s", data.toString()));
                        Message.getMessage(data.toString()).receive(device);
                    }
                } catch (IOException e) {
                    final String message = e.getMessage();
                    if (message.contains("Connection reset by peer") ||
                            message.contains("Connection timed out")) {
                        reset = true;
                        Log.i(tag(this), "Connection has expired");
                        break;
                    }
                    Log.e(tag(this), "Unable to read message", e);
                    disconnect();
                }
            }
            if (reset) { reset(); }
        }
    }
}
