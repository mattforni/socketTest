package com.example.sockettest.network.output;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import android.util.Log;

import com.example.sockettest.music.Song;
import com.google.gson.JsonElement;

public class PublishLibrary extends OutputMessage {
    public static final int CODE = 2;

    private final JsonElement message;

    public PublishLibrary(final List<Song> library) {
        this.message = Serializer.publishLibrary(library);
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            channel.write(ByteBuffer.wrap(message.toString().getBytes()));
            Log.i(tag(this), format("Publishing Library"));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }
}
