package com.example.sockettest.network;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.network.Message.InputMessage;
import com.example.sockettest.network.Message.OutputMessage;

public class NetworkLayer {
    private final Device device;
    private final Socket socket;

    private InputStream input;
    private OutputStream output;
    private boolean shutdown;

    public NetworkLayer(final Device device, final Socket socket) {
        this.device = device;
        this.socket = socket;
        this.shutdown = false;

        try {
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();

            // Begin listening on the input stream
            new InputListener().start();
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to open I/O"), e);
        }
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

    private class InputListener extends Thread {
        private final byte[] codeBuffer = new byte[1];

        @Override
        public void run() {
            while(!shutdown) {
                receiveMessage(readCode());
            }
        }

        private byte readCode() {
            try {
                input.read(codeBuffer);
            } catch (IOException e) {
                Log.e(tag(this), format("Unable to read code"), e);
            }
            return codeBuffer[0];
        }
    }
}
