package com.example.sockettest.server;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.util.Log;

import com.example.sockettest.network.NetworkLayer;
import com.example.sockettest.network.Message.OutputMessage;
import com.example.sockettest.network.output.PublishClientId;
import com.google.common.collect.Maps;

public class ClientManager extends Thread {
    private final Map<String, NetworkLayer> clientMap;
    private final Server server;

    private boolean running;
    private ServerSocket socket;

    public ClientManager(final Server server) {
        this.server = server;
        this.clientMap = Maps.newLinkedHashMap();
        this.running = false;
    }

    @Override
    public final void run() {
        if (socket == null) { Log.w(tag(this), "Try calling start(address, port)"); }
        if (running) { return; }

        Log.i(tag(this), format("Listening for clients at: %s", socket.getLocalSocketAddress()));
        running = true;

        // This is the main loop which waits for and handles incoming connections
        while (!isInterrupted()) {
            try {
                final String clientId = generateUUID();
                clientMap.put(clientId, new NetworkLayer(server, socket.accept()));
                publishMessage(clientId, new PublishClientId(clientId));
                Log.i(tag(this), format("Accepted new client with ID: %s", clientId));
            } catch (IOException e) {
                Log.e(tag(this), "Unable to accept a new client", e);
            }
        }
    }

    public final void publishMessage(final String clientId, final OutputMessage message) {
        final NetworkLayer network = clientMap.get(clientId);
        if (network == null) {
            Log.e(tag(this), format("Unable to find network for %s", clientId));
            return;
        }
        network.publishMessage(message);
    }

    public final void publishMessage(final List<String> clientIds, final OutputMessage message) {
        for (final String clientId : clientIds) { publishMessage(clientId, message); }
    }

    public final void start(final String address, final int port) throws IOException {
        socket = initializeSocket(address, port);
        start();
    }

    private ServerSocket initializeSocket(final String address, final int port) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        ServerSocket server = new ServerSocket();
        server.setReuseAddress(true);
        server.bind(socketAddress);
        return server;
    }

    private String generateUUID() {
        final Set<String> usedIds = clientMap.keySet();
        String id = UUID.randomUUID().toString();
        // This may not be necessary but for now its for safety.
        while (usedIds.contains(id)) { id = UUID.randomUUID().toString(); }
        return id;
    }
}
