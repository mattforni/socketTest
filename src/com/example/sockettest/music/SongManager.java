package com.example.sockettest.music;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.os.Environment;
import android.util.Log;

import com.example.sockettest.Device;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SongManager {
    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().toString();
    private static final String[] EXTENSIONS = new String[] {"mp3", "MP3", "m4a", "M4A"};
    private static final String LIBRARY_DIR = EXTERNAL_PATH + "/DELTA";
    private static final File LIBRARY_FILE = new File(LIBRARY_DIR, "MusicList.txt");

    private final Device device;
    private final List<Song> allSongs, localSongs, playlist;

    public SongManager(final Device device) {
        this.device = device;

        this.allSongs = Lists.newArrayListWithExpectedSize(127);
        this.localSongs = Lists.newArrayListWithExpectedSize(127);
        this.playlist = Lists.newArrayListWithExpectedSize(127);

        initializeLibraryFile();
        loadLocalMusic();
    }

    public final void enqueue(final Song song) {
        playlist.add(song);
    }

    public final ImmutableList<Song> getAllSongs() {
        return ImmutableList.copyOf(allSongs);
    }

    public final ImmutableList<Song> getLocalSongs() {
        return ImmutableList.copyOf(localSongs);
    }

    public final ImmutableList<Song> getPlaylist() {
        return ImmutableList.copyOf(playlist);
    }

    public final Song getSong(final int index) throws UnknownSongException {
        if (index < 0 || index >= allSongs.size()) { throw new UnknownSongException(index); }
        return allSongs.get(index);
    }

    public final boolean isEmpty() {
        return allSongs.isEmpty();
    }

    public final int numSongs() {
        return allSongs.size();
    }

    private void initializeLibraryFile() {
        // If the library directory does not exist create it
        final File libraryDir = new File(LIBRARY_DIR);
        if (!libraryDir.exists()) { libraryDir.mkdirs(); }

        // If the library file does not exist create it
        if(!LIBRARY_FILE.exists()) {
            try {
                LIBRARY_FILE.createNewFile();
                final FileWriter writer = new FileWriter(LIBRARY_FILE);
                writer.write("0\n");
                writer.close();
            } catch (IOException e) {
                Log.e(tag(this), format("Unable to initialize: %s", LIBRARY_FILE.getAbsolutePath()), e);
            }
        }
    }

    private void loadLocalMusic() {
        for(final File file : FileUtils.listFiles(new File(EXTERNAL_PATH), EXTENSIONS, true)) {
            try {
                localSongs.add(Song.parse(device, file));
            } catch (Exception e) {
                Log.e(tag(this), format("Unable to parse song from: %s", file), e);
                continue;
            }
        }
        allSongs.addAll(localSongs);
    }

    private void loadLibraryFile(final boolean reload) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(LIBRARY_FILE));
            final int size = Integer.parseInt(reader.readLine());
            if (reload) { allSongs.clear(); }

            // TODO read in from file and make into a song

            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeLibraryFile() {
        // TODO be able to write library to a file
    }

    @SuppressWarnings("serial")
    public static class UnknownSongException extends IllegalArgumentException {
        private static final String ERROR_FORMAT = "Unable to find song with index %d";

        public UnknownSongException(final int index) {
            super(String.format(ERROR_FORMAT, index));
        }
    }
}
