package com.example.sockettest.actions;

import java.io.IOException;
import java.util.List;

import android.util.Log;

import com.example.sockettest.network.NetworkLayer;
import com.google.common.collect.Lists;

public abstract class Action {
    public static final List<Integer> CLIENT_CODES = Lists.newArrayList(0, 1, 5);

    public abstract String getIdentifier();
    public abstract void perform(final NetworkLayer network);

    protected void handleException(final IOException e) {
        Log.e(getIdentifier(), "Unable to perform action", e);
    }

    public static class Factory {
        public static Action getAction(final int code, final NetworkLayer network) {
            Action action = null;
            switch(code) {
                // InitializeClient action
                case 0:
                    action = new InitializeClient();
                    break;
                case 1:
                    // TODO not quite sure what this code indicates
//                    inputStream.read(buffer);
//                    curr = (char) buffer[0];
//                                                    
//                    while(curr != '\0') {
//                        accum += curr;
//                        inputStream.read(buffer);
//                        curr = (char) buffer[0];
//                    }
//    
//                    JsonSongDeserializer deserializer = new JsonSongDeserializer();
//                    JsonElement jsonElement = new JsonParser().parse(accum);
//    
//                    enqueuedSong = deserializer.deserialize(jsonElement, null, null);
//                    Message msg2 = Message.obtain();
//                    playlistHandler.sendMessage(msg2);
                    break;
                case 5:
                    // TODO not quite sure what this code indicates
//                    Log.w("SERVERTHREAD","CODE 5 RECIEVED");
//                    disconnecting = true;
//                    clientSocket.close();
//                    Message msg1 = Message.obtain();
//                    disconnectHandler.sendMessage(msg1);
                    break;
                default:
                    // TODO come up with some default logic
                    break;
            }
            return action;
        }
    }
}
