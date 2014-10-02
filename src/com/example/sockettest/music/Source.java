package com.example.sockettest.music;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public enum Source {
    LIBRARY, PLAYLIST, SEARCH;

    private final List<Song> songs = Lists.newArrayList();

    public final synchronized void add(final Song song) {
        songs.add(song);
    }

    public final synchronized void add(final Collection<Song> songs) {
        this.songs.addAll(songs);
    }

    public final synchronized ImmutableList<Song> all() {
        return ImmutableList.copyOf(songs);
    }

    public final synchronized void clear() {
        songs.clear();
    }

    public final synchronized Song get(final int index) throws UnknownSongException {
        if (index < 0 || index >= songs.size()) { throw new UnknownSongException(index); }
        return songs.get(index);
    }

    public final synchronized int numSongs() {
        return songs.size();
    }
    
    public final synchronized boolean isEmpty() {
    	return songs.isEmpty();
    }

    public final synchronized void update(final List<Song> songs) {
        clear();
        add(songs);
    }
    
    public final synchronized void append(final List<Song> songs) {
        add(songs);
    }

    @SuppressWarnings("serial")
    public static class UnknownSongException extends IllegalArgumentException {
        private static final String ERROR_FORMAT = "Unable to find song with index %d";

        public UnknownSongException(final int index) {
            super(String.format(ERROR_FORMAT, index));
        }
    }
}
