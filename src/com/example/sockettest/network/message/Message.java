package com.example.sockettest.network.message;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.example.sockettest.Device;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class Message {
    public static final String CODE_KEY = "CODE";

    private static final JsonParser PARSER = new JsonParser();
    private static final Pattern PATTERN = Pattern.compile("\\{|\\}");

    public abstract void publish(final SocketChannel channel);
    public abstract void receive(final Device device);
    public abstract String serialize();

    public static final List<Message> deserialize(final String data) {
        if (data == null) { return ImmutableList.of(); }
        List<Message> messages = Lists.newLinkedList();
        for (final String message : separate(data)) {
            final JsonObject jsonObject = PARSER.parse(message).getAsJsonObject();
            final int code = jsonObject.get(CODE_KEY).getAsInt();
            switch(code) {
                case ClientIdMessage.CODE: // 1
                    messages.add(ClientIdMessage.deserialize(jsonObject));
                    break;
                case LibraryMessage.CODE: // 2
                    messages.add(LibraryMessage.deserialize(jsonObject));
                    break;
                case PlaylistMessage.CODE: // 3
                    messages.add(PlaylistMessage.deserialize(jsonObject));
                    break;
                case CurrentSongMessage.CODE: // 4
                    messages.add(CurrentSongMessage.deserialize(jsonObject));
                    break;
                default:
                    Log.e(tag(Message.class), format("Unrecognized Code: %d", code));
            }
        }
        return ImmutableList.copyOf(messages);
    }

    public static final List<String> separate(final String data) {
        if (data == null) { return ImmutableList.of(); }
        int brackets = 0, start = -1;
        final List<String> messages = Lists.newLinkedList();
        final Matcher matcher = PATTERN.matcher(data);
        while (matcher.find()) {
            final int index = matcher.start();
            final char c = data.charAt(index);
            if (c == '{') {
                if (brackets == 0) { start = index; }
                brackets++;
            } else if (c == '}') {
                brackets--;
                if (brackets == 0) {
                    try {
                        messages.add(data.substring(start, index+1));
                    } catch (IndexOutOfBoundsException e) {
                        // We don't want to fail all messages if only one is malformed
                        // TODO add to a failed messages queue or something
                    }
                }
            }
        }
        return ImmutableList.copyOf(messages);
    }
}
