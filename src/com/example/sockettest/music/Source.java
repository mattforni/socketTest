package com.example.sockettest.music;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Source {
    private final List<Song> songs = Lists.newArrayList();

    public final void add(final Song song) {
        songs.add(song);
    }

    public final void add(final Collection<Song> songs) {
        this.songs.addAll(songs);
    }

    public final ImmutableList<Song> all() {
        return ImmutableList.copyOf(songs);
    }

    public final void clear() {
        songs.clear();
    }

    public final Song get(final int index) throws UnknownSongException {
        if (index < 0 || index >= songs.size()) { throw new UnknownSongException(index); }
        return songs.get(index);
    }

    public final int numSongs() {
        return songs.size();
    }

    public enum Type {
        LIBRARY, PLAYLIST;
    }

    @SuppressWarnings("serial")
    public static class UnknownSongException extends IllegalArgumentException {
        private static final String ERROR_FORMAT = "Unable to find song with index %d";

        public UnknownSongException(final int index) {
            super(String.format(ERROR_FORMAT, index));
        }
    }
}
