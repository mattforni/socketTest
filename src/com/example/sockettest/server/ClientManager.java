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

import com.example.sockettest.music.Song;
import com.example.sockettest.network.NetworkLayer;
import com.example.sockettest.network.message.ClientIdMessage;
import com.example.sockettest.network.message.CurrentSongMessage;
import com.example.sockettest.network.message.LibraryMessage;
import com.example.sockettest.network.message.Message;
import com.example.sockettest.network.message.PlaylistMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ClientManager extends Thread {
    private final Server server;
    private final Map<String, NetworkLayer> clientMap;
    private final List<String> clientList;
    private final ServerSocketChannel serverChannel;

    public ClientManager(final Server server, final String address, final int port) {
        this.server = server;
        this.clientMap = Maps.newLinkedHashMap();
        this.clientList = Lists.newArrayList();
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
                clientList.add(clientId);
                publishMessage(clientId, new ClientIdMessage(clientId, false));
                //initializeClient(clientId);
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
    
    private synchronized void initializeClient(String clientId) {
    	if (server.getCurrentSong() != null)
    		publishMessage(clientId, new CurrentSongMessage(server.getCurrentSong()));
    	if (!server.getPlaylist().isEmpty())
    		publishMessage(clientId, new PlaylistMessage(server.getPlaylist()));
	}

	public final void publishCurrentSong(Song song) {
        publishMessage(new CurrentSongMessage(song));
    }
    
    public final void publishLibrary(List<Song> library) {
        publishMessage(new LibraryMessage(library));
    }
    
    public final void publishPlaylist(List<Song> playlist) {
        publishMessage(new PlaylistMessage(playlist));
    }
    
    public final void publishMessage(final String clientId, final Message message) {
        final NetworkLayer network = clientMap.get(clientId);
        if (network == null) {
            Log.e(tag(this), format("Unable to find network for %s", clientId));
            return;
        }
        network.publishMessage(message);
    }

    public final void publishMessage(final Message message) {
        for (final String clientId : clientList) { publishMessage(clientId, message); }
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
