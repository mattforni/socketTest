package com.example.sockettest.utils;

import java.util.List;
import java.util.Map;

import com.example.sockettest.music.Song;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class Songs {
    public static final List<Map<String, String>> toListOfMaps(final List<Song> songs) {
        final List<Map<String, String>> list = Lists.newArrayListWithExpectedSize(songs.size());
        for (final Song song : songs) {
            list.add(song.toMap());
        }
        return ImmutableList.copyOf(list);
    }
}
