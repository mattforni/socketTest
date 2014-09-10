package com.example.sockettest.network;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

import com.example.sockettest.actions.Action;

public class NetworkLayer {
    private boolean disconnecting, initialized;
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public NetworkLayer(final Socket socket) {
        this.disconnecting = false;
        this.initialized = false;
        this.socket = socket;
        try {
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();

            // Begin listening on the input and output streams
            new InputListener(this).run();
        } catch (IOException e) {
            Log.e(tag(this), format("Unable to open I/O"), e);
        }
    }

    public final void disconnect() {
        disconnecting = true;
        try {
            socket.close();
//          Message msg1 = Message.obtain();
//          disconnectHandler.sendMessage(msg1);
        } catch (IOException e) {
            disconnecting = false;
            Log.e(tag(this), format("Unable to close I/O"), e);
        }

    }

    public final InputStream input() { return input; }
    public final boolean isDisconnecting() { return disconnecting; }
    public final boolean isInitialized() { return initialized; }
    public final OutputStream output() { return output; }
    public final void setInitialized(final boolean initialized) { this.initialized = initialized; }

    private class InputListener extends Thread {
        private final byte[] codeBuffer = new byte[1];
        private final NetworkLayer network;

        public InputListener(final NetworkLayer network) {
            this.network = network;
        }

        @Override
        public void run() {
            // TODO need a break condition for this while
            while(true) {
                Action action = Action.Factory.getAction(readCode(), network);
                // TODO submit this action the 'input' executor service to be performed
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

    // TODO create a similar private class for the OutputListener
}
