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

import com.example.sockettest.network.NetworkLayer;
import com.example.sockettest.network.output.OutputMessage;
import com.example.sockettest.network.output.PublishClientId;
import com.google.common.collect.Maps;

public class ClientManager extends Thread {
    private final Server server;
    private final Map<String, NetworkLayer> clientMap;
    private final ServerSocketChannel serverChannel;


    public ClientManager(final Server server, final String address, final int port) {
        this.server = server;
        this.clientMap = Maps.newLinkedHashMap();
        this.serverChannel = initializeChannel(address, port);
    }

    @Override
    public final void run() {
        try {
            // This is the main loop which waits for and handles incoming connections
            while (!isInterrupted()) {
                final SocketChannel channel = serverChannel.accept();
                channel.socket().setKeepAlive(true);
                channel.socket().setSoTimeout(0);
                final String clientId = generateUUID();
                clientMap.put(clientId, new NetworkLayer(server, channel));
                publishMessage(clientId, new PublishClientId(clientId));
                Log.i(tag(this), format("Accepted new client with ID: %s", clientId));
            }
        } catch (IOException e) {
            Log.e(tag(this), "Unable to accept new clients", e);
        } finally {
            try {
                serverChannel.close();
            } catch (IOException e) {
                Log.e(tag(this), "Unable to close server channel");
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

    private ServerSocketChannel initializeChannel(final String address, final int port) {
        ServerSocketChannel channel = null;
        try {
            channel = ServerSocketChannel.open();
            channel.configureBlocking(true);
            channel.socket().setSoTimeout(0);
            channel.socket().bind(new InetSocketAddress(address, port));
        } catch (IOException e) {
            Log.e(tag(this), "Unable to initialize server channel", e);
            System.exit(1);
        }
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
