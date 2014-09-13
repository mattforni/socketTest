package com.example.sockettest.music;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;

import com.example.sockettest.Device;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

public abstract class Song {
    public static final String ARTIST_KEY = "ARTIST";
    public static final String OWNER_KEY = "OWNER_ID";
    public static final String PATH_KEY = "PATH";
    public static final String TITLE_KEY = "TITLE";
    public static final String TYPE_KEY = "TYPE";

    private static final int NUM_MAP_FIELDS = 2;
    private static final String UNKNOWN_ARTIST = "unknown artist";

    protected final String owner, path, artist, title;
    protected final Type type;

    protected Song(final String owner, final String path,
            final String artist, final String title, final Type type) {
        this.owner = owner;
        this.path = path;
        this.artist = artist;
        this.title = title;
        this.type = type;
    }

    public final String getArtist() {
        return artist;
    }

    public final String getPath() {
        return path;
    }

    public final String getTitle() {
        return title;
    }

    public final Type getType() {
        return type;
    }

    public final boolean isLocal(final Device device) {
        return owner.equals(device.getId());
    }

    public final Map<String, String> toMap() {
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(NUM_MAP_FIELDS);
        map.put(ARTIST_KEY, getArtist());
        map.put(TITLE_KEY, getTitle());
        return ImmutableMap.copyOf(map);
    }

    public static Song parse(final Device device, final File file) throws CannotReadException,
            IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
        if (Type.MP3.isType(file)) { // MP3 file handling
            return MP3Song.parse(device, file);
        } else if (Type.M4A.isType(file)) { // M4A file handling
            return M4ASong.parse(device, file);
        } else {
            return null;
        }
    }

    public static Song parse(final JsonObject json) {
        final String owner = json.get(OWNER_KEY).getAsString();
        final String path = json.get(PATH_KEY).getAsString();
        final String artist = json.get(ARTIST_KEY).getAsString();
        final String title = json.get(TITLE_KEY).getAsString();
        final Type type = Type.valueOf(json.get(TYPE_KEY).getAsString());
        switch (type) {
            case MP3:
                return new MP3Song(owner, path, artist, title);
            case M4A:
                return new M4ASong(owner, path, artist, title);
            default:
                throw new IllegalArgumentException("Malformed song json");
        }
    }

    public static enum Type {
        MP3 (Lists.newArrayList("mp3", "MP3")),
        M4A (Lists.newArrayList("m4a", "M4A"));

        private final List<String> extensions;

        private Type(final List<String> extensions) {
            this.extensions = extensions;
        }

        public final boolean isType(final File file) {
            if (file == null) { return false; }
            final String extension = FilenameUtils.getExtension(file.getAbsolutePath());
            return extensions.contains(extension);
        }
    }

    public static class MP3Song extends Song {
        public MP3Song(final String owner, final String path,
                final String artist, final String title) {
            super(owner, path, artist, title, Type.MP3);
        }

        public static final Song parse(final Device device, final File file) throws CannotReadException,
                IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
            final AudioFile audioFile = AudioFileIO.read(file);
            final Tag tag = audioFile.getTag();

            final String path = file.getAbsolutePath();
            String title = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);

            if(title == null || title.equals("")) { title = file.getName(); }
            if(artist == null || artist.equals("")) { artist = UNKNOWN_ARTIST; }

            return new MP3Song(device.getId(), path, artist, title);
        }
    }

    public static class M4ASong extends Song {
        public M4ASong(final String owner, final String path,
                final String artist, final String title) {
            super(owner, path, artist, title, Type.M4A);
        }

        public static final Song parse(final Device device, final File file) throws CannotReadException,
                IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
            final AudioFile audioFile = AudioFileIO.read(file);
            final Mp4Tag mp4tag = (Mp4Tag)audioFile.getTag();

            final String path = file.getAbsolutePath();
            String title = mp4tag.getFirst(Mp4FieldKey.TITLE);
            String artist = mp4tag.getFirst(Mp4FieldKey.ARTIST);

            if(title == null || title.equals("")) { title = file.getName(); }
            if(artist == null || artist.equals("")) { artist = UNKNOWN_ARTIST; }

            return new M4ASong(device.getId(), path, artist, title);
        }
    }
}
