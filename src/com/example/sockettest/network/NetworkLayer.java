package com.example.sockettest.network;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.Message.InputMessage;
import com.example.sockettest.network.Message.OutputMessage;

public class NetworkLayer {
    private final Device device;

    private String address;
    private InputStream input;
    private OutputStream output;
    private int port;
    private Socket socket;
    private boolean shutdown;

    public NetworkLayer(final Device device, final Socket socket) {
        this.device = device;
        this.socket = socket;
        this.shutdown = false;
        new Connection().run();
    }

    public NetworkLayer(final Device device, final String address, final int port) {
        this.device = device;
        this.address = address;
        this.port = port;
        this.shutdown = false;
        new Connection().run();
    }

    public final void disconnect() {
        try {
            socket.close();
            shutdown = true;
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to close I/O"), e);
        }
    }

    public final synchronized void publishMessage(final OutputMessage message) {
        message.publish(output);
    }

    public final synchronized void receiveMessage(final byte code) {
        final InputMessage message = InputMessage.getMessage(code);
        message.receive(input, device);
    }

    private class Connection extends Thread {
        @Override
        public void run() {
            try {
                if (socket == null) { socket = new Socket(address, port); }
                input = socket.getInputStream();
                output = socket.getOutputStream();

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
                receiveMessage(readCode());
            }
        }

        private byte readCode() {
            final byte[] buffer = new byte[1];
            try {
                input.read(buffer);
            } catch (IOException e) {
                Log.e(tag(this), format("Unable to read code"), e);
            }
            return buffer[0];
        }
    }
}
