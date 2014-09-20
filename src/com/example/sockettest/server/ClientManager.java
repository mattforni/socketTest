package com.example.sockettest.server;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.util.Log;

import com.example.sockettest.network.Message.OutputMessage;
import com.example.sockettest.network.NetworkLayer;
import com.example.sockettest.network.output.PublishClientId;
import com.google.common.collect.Maps;

public class ClientManager extends Thread {
    private final Map<String, NetworkLayer> clientMap;
    private final Server server;

    private boolean running;
    private ServerSocketChannel serverChannel;

    public ClientManager(final Server server) {
        this.server = server;
        this.clientMap = Maps.newLinkedHashMap();
        this.running = false;
    }

    @Override
    public final void run() {
        if (serverChannel == null) { Log.w(tag(this), "Try calling start(address, port)"); }
        if (running) { return; }

        Log.i(tag(this), format("Listening at: %s", serverChannel.socket().getLocalSocketAddress()));
        running = true;

        // This is the main loop which waits for and handles incoming connections
        while (!isInterrupted()) {
            try {
                final String clientId = generateUUID();
                final SocketChannel channel = serverChannel.accept();
                clientMap.put(clientId, new NetworkLayer(server, channel));
                publishMessage(clientId, new PublishClientId(clientId));
                Log.i(tag(this), format("Accepted new client with ID: %s", clientId));
            } catch (IOException e) {
                Log.e(tag(this), "Unable to accept a new client", e);
            } finally {
                try {
                    serverChannel.close();
                } catch (IOException e) {
                    Log.e(tag(this), "Unable to close server socket channel");
                }
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
        serverChannel = initializeServerChannel(address, port);
        start();
    }

    private ServerSocketChannel initializeServerChannel(
            final String address, final int port) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(true);
        channel.socket().bind(new InetSocketAddress(address, port));
        return channel;
    }

    private String generateUUID() {
        final Set<String> usedIds = clientMap.keySet();
        String id = UUID.randomUUID().toString();
        // This may not be necessary but for now its for safety.
        while (usedIds.contains(id)) { id = UUID.randomUUID().toString(); }
        return id;
    }
}
