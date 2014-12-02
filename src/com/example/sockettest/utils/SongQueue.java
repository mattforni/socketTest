package com.example.sockettest.utils;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.util.LinkedList;
import java.util.Random;

import android.util.Log;

import com.example.sockettest.music.Song;
import com.example.sockettest.music.Source;
import com.example.sockettest.utils.SongQueue.SongSource;

@SuppressWarnings({"serial", "unused"})
public class SongQueue extends LinkedList<SongSource> {
    // TODO eventually incorporate RANDOM
    private static final Random RANDOM = new Random();

    private final int capacity;

    public SongQueue(final int capacity) {
        super();
        this.capacity = capacity;
    }

    public final int getCapacity() {
        return capacity;
    }

    public final Song getCurrent() {
        final SongSource current = peekLast();
        if (current == null) { return null; }
        return current.getSong();
    }

    public final Song getNext(final boolean shuffle) {
        // TODO eventually include 'shuffle' logic here

        final SongSource current = peekLast();
        if (current == null) { // If nothing has been played yet.
            if (!Source.PLAYLIST.isEmpty()) { // Use playlist as the default source.
                return push(Source.PLAYLIST, 0);
            } else if (!Source.LIBRARY.isEmpty()) { // Fall back to the libraray as a source.
                return push(Source.LIBRARY, 0);
            } else { // If both are empty return null.
                // TODO possibly throw an exception at this level
                return null;
            }
        } else { // Else the queue already has one or more songs in it.
            final Source currentSource = current.source;
            final int nextIndex = current.index + 1;
            if (nextIndex < currentSource.numSongs()) {
                return push(currentSource, nextIndex);
            } else {
                // TODO eventually include 'repeat' logic here, for now always loop back
                return push(currentSource, 0);
            }
        }
    }

    public final Song getPrevious() {
        if (size() < 2) { return null; } // If there is less than two songs do nothing.
        final SongSource current = pollLast(); // Retrieve and remove the last song.
        return peekLast().getSong(); // Retrieve and return the *new* last song.
    }

    public final boolean isPlaying(final Song song) {
        final SongSource current = peekLast();
        if (current == null) { return false; }
        return (song.equals(current.getSong()));
    }

    private Song push(final Source source, final int index) {
        if (size() == getCapacity()) {
            final SongSource removed = removeFirst();
            Log.i(tag(this), format("Removed '%s' from the queue", removed.getSong()));
        }
        final SongSource pushed = new SongSource(source, index);
        super.push(pushed);
        return pushed.getSong();
    }

    protected static class SongSource {
        private final int index;
        private final Source source;

        public SongSource(final Source source, final int index) {
            this.index = index;
            this.source = source;
        }

        public final Song getSong() {
            return source.get(index);
        }
    }
}
