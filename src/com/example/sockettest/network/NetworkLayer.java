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
import com.example.sockettest.network.Message.InputMessage;
import com.example.sockettest.network.Message.OutputMessage;
import com.example.sockettest.network.Message.UnknownMessage;

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

    public final void disconnect() {
        try {
            channel.close();
            shutdown = true;
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to close I/O"), e);
        }
    }

    public final synchronized void publishMessage(final OutputMessage message) {
        message.publish(channel);
    }

    public final synchronized void reset() {
        try {
            channel.close();
            new Connection().start();
        } catch (IOException e) {
            Log.e(tag(this), "Unable to reset connection");
        }
    }

    private class Connection extends Thread {
        @Override
        public void run() {
            try {
                if (channel == null) {
                    channel = SocketChannel.open(new InetSocketAddress(address, port));
                    channel.configureBlocking(true);
                }

                // Begin listening on the input stream
                new InputListener().start();
            } catch (UnknownHostException e) {
                Log.e(tag(this), format("Could not find server at %s:%d", address, port));
                System.exit(1);
            } catch (IOException e) {
                Log.e(tag(this), format("Unable to initialize network on %s:%d", address, port));
                System.exit(1);
            }
        }
    }

    private class InputListener extends Thread {
        @Override
        public void run() {
            while(!shutdown) {
                try {
                    InputMessage.getMessage(readCode()).receive(channel, device);
                } catch (UnknownMessage e) {
                    if (e.isReset()) {
                        reset();
                        continue;
                    }
                    Log.e(tag(this), e.getMessage());
                }
            }
        }

        private int readCode() {
            try {
                channel.read(BUFFER);
            } catch (IOException e) {
                Log.e(tag(this), format("Unable to read code"), e);
            }
            int code = BUFFER.getInt();
            BUFFER.clear();
            return code;
        }
    }
}
