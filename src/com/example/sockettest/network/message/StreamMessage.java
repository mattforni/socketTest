package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.google.gson.JsonObject;

public class StreamMessage extends Message {
    public static final int CODE = 5;
    public static final String INDEX_CODE = "INDEX";
    
    private static final int STEP_SIZE = 1000;
    private int frameIndex;
    
    /*
     * byte[] bytes = getByteArr();
     * String base64String = Base64.encodeBase64String(bytes);
     * byte[] backToBytes = Base64.decodeBase64(base64String);
     */
    
    private final Song song;
    
    public StreamMessage(final Song song) {
        this.song = song;
        this.frameIndex = 0;
        
    }

    @Override
    public final void publish(final SocketChannel channel) {
        try {
            final String message = serialize();
            channel.write(ByteBuffer.wrap(message.getBytes()));
            Log.i(tag(this), format("Streaming song %s", song.toString()));
        } catch (IOException e) {
            Log.w(tag(this), "Unable to write to channel", e);
        }
    }

    @Override
    public final void receive(final Device device) {
        if (song != null) {
            device.stream(song);
            Log.i(tag(this), format("Streaming song %s", song.toString()));
        } else {
            Log.e(tag(this), "Tried to stream song");
        }
    }
    
    public final String serialize() {
		JsonObject jsonObject = Song.serialize(song);
		jsonObject.addProperty(INDEX_CODE,frameIndex);
		return jsonObject.toString();
    }

    public final static StreamMessage deserialize(final JsonObject data) {
    	try {
            return new StreamMessage(Song.deserialize(data));
        } catch (IllegalStateException e) {
            Log.w(tag(StreamMessage.class), format("Unable to stream data"));
            return null;
        }
    }
}
