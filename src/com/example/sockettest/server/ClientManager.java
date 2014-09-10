package com.example.sockettest.server;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.util.Log;

import com.example.sockettest.actions.SendClientId;
import com.example.sockettest.network.NetworkLayer;
import com.google.common.collect.Maps;

public class ClientManager extends Thread {
    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 8080;

    private final Map<String, NetworkLayer> clientMap;
    private final ServerSocket server;

    public ClientManager(final String address, final int port) throws IOException {
        this.clientMap = Maps.newLinkedHashMap();
        this.server = initializeServer(address, port);
    }

    @Override
    public final void run() {
        Log.i(tag(this), format("Listening for clients at: %s", server.getLocalSocketAddress()));

        // This is the main loop which waits for and handles incoming connections
        while (!isInterrupted()) {
            try {
                final String clientId = generateUUID();
                final NetworkLayer network = new NetworkLayer(server.accept());
                new SendClientId(clientId).perform(network);
                clientMap.put(clientId, network);
                Log.i(tag(this), format("Accepted new client with ID: %s", clientId));
            } catch (IOException e) {
                Log.e(tag(this), "Unexpectedly unable to accept a new client", e);
            }
        }
    }

    private ServerSocket initializeServer(final String address, final int port) throws IOException {
        try {
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            ServerSocket server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(socketAddress);
            return server;
        } catch (IOException e) {
            // If unable to initialize on the default address and port throw the exception.
            if (address.equals(DEFAULT_ADDRESS) && port == DEFAULT_PORT) { throw e; }

            // Otherwise log and recursively try to initialize on the default address and port.
            Log.w(tag(this), format("Could not initalize server at %s:%d", address, port), e);
            return initializeServer(DEFAULT_ADDRESS, DEFAULT_PORT);
        }
    }

    private String generateUUID() {
        final Set<String> usedIds = clientMap.keySet();
        String id = UUID.randomUUID().toString();
        // This may not be necessary but for now its for safety.
        while (usedIds.contains(id)) { id = UUID.randomUUID().toString(); }
        return id;
    }
}
