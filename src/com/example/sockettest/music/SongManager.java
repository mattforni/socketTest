package com.example.sockettest.music;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import android.os.Environment;
import android.util.Log;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song.Format;
import com.example.sockettest.music.Song.M4ASong;
import com.example.sockettest.music.Song.MP3Song;
import com.example.sockettest.music.Source.UnknownSongException;
import com.example.sockettest.utils.MaxStack;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SongManager {
    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().toString();
    private static final String[] EXTENSIONS = new String[] {"mp3", "MP3", "m4a", "M4A"};
    private static final String LIBRARY_DIR = EXTERNAL_PATH + "/Delta";
    private static final File LIBRARY_FILE = new File(LIBRARY_DIR, "local_songs.txt");
    private static final Random RANDOM = new Random();
    private static final int MAX_PLAYED_SONGS = 30;

    private final Device device;
    private final List<Song> localSongs;
    // TODO Make stack that only holds ~30 last played songs
    private final MaxStack<Song> playedSongs;
    private int currentIndex;
    private Song currentSong;
    private int playlistIndex;
    private boolean shuffle;

    public SongManager(final Device device) {
        this.device = device;
        this.localSongs = Lists.newArrayListWithExpectedSize(127);
        
        this.playedSongs = new MaxStack<Song>(MAX_PLAYED_SONGS);
        
        this.currentIndex = 0;
        this.playlistIndex = 0;

        this.shuffle = false;
    }

    public final int current() {
        return currentIndex;
    }

    public final void enqueue(final Song song) {
        Source.PLAYLIST.add(song);
    }
    
    public final void enqueue(final List<Song> songs) {
        for(Song song : songs) {
        	enqueue(song);
        }
    }

    public final ImmutableList<Song> getAllSongs() {
        return Source.LIBRARY.all();
    }

    public final ImmutableList<Song> getPlaylist() {
        return Source.PLAYLIST.all();
    }

    public final ImmutableList<Song> getSearchResults() {
        return Source.SEARCH.all();
    }

    public final Song getNext() {
    	Song song = null;
    	
    	if (isEmpty()) { return song; }
        
        if(!Source.PLAYLIST.isEmpty()) {
        	// TODO "pop" the playlist
	        song = Source.PLAYLIST.get(playlistIndex++);
        } else {
        	if (shuffle) {
	        	currentIndex = RANDOM.nextInt(Source.LIBRARY.numSongs());
	        	song = Source.LIBRARY.get(currentIndex);
	        } else {
	        	song = Source.LIBRARY.get(currentIndex);
	            currentIndex = (currentIndex + 1) >= Source.LIBRARY.numSongs() ? 0 : currentIndex + 1;
	        }
        }
		return song;
    }
    
    public final Song getPrevious() {
    	Song song = null;
    	if(!playedSongs.isEmpty()) {
    		song = playedSongs.pop();
    	} else {
    		if(shuffle) {
    			currentIndex = RANDOM.nextInt(Source.LIBRARY.numSongs());
    			song = Source.LIBRARY.get(currentIndex);
    		} else {
    			song = Source.LIBRARY.get(currentIndex);
    			currentIndex = (currentIndex - 1) < 0 ? Source.LIBRARY.numSongs() - 1 : currentIndex - 1;
    		}
    	}
    	return song;
    }
    
    public final void pushPrevious(final Song song) {
    	Log.i(tag(this), format("Pushing '%s'", song.getTitle()));
    	playedSongs.push(song);
    }
    
    public final Song getSong(final Source source, final int index) throws UnknownSongException {
        return source.get(index);
    }
    
    public final Song getCurrentSong() {
    	return currentSong;
    }
    
    public final void setCurrentSong(Song song) {
    	currentSong = song;
    }
    
    public final boolean isEmpty() {
        return Source.LIBRARY.numSongs() == 0;
    }

    public final boolean isPlaying(final Source source, final Song song) {
        // TODO will need to integrate source
        return currentIndex >= 0 && Source.LIBRARY.get(currentIndex).equals(song);
    }

    public final void loadLibrary() {
        if (!LIBRARY_FILE.exists()) {
            loadLocalMusic();
            new LibraryWriter().start();
        } else {
            loadLibraryFile();
        }
    }

    public final int numSongs() {
        return Source.LIBRARY.numSongs();
    }

    public final List<Song> search(final String query) {
        Source.SEARCH.clear();
        final String[] queryFragments = query.toLowerCase(Locale.ENGLISH).split("\\s+");
        
        for (final Song song : Source.LIBRARY.all()) {
            final String artist = song.getArtist().toLowerCase(Locale.ENGLISH);
            final String title = song.getTitle().toLowerCase(Locale.ENGLISH);
            
            boolean containsAll = true;
            
            for(String queryFragment : queryFragments) {
            	if(!title.contains(queryFragment) && !artist.contains(queryFragment)) {
            		containsAll = false;
            		break;
            	}
            }
            if(containsAll) {
            	Source.SEARCH.add(song);
            }
        }
        return getSearchResults();
    }

    private void loadLibraryFile() {
        Log.i(tag(this), format("Loading music from '%s'", LIBRARY_FILE.getAbsolutePath()));

        BufferedReader reader = null;
        try {
            final List<Song> newSongs = Lists.newLinkedList();
            reader = new BufferedReader(new FileReader(LIBRARY_FILE));
            final String owner = device.getId();

            final int size = Integer.parseInt(reader.readLine());
            for(int i = 0; i < size; i++) {
                String path = reader.readLine();
                Format format = Format.valueOf(reader.readLine());
                String title = reader.readLine();
                String artist = reader.readLine();
                switch (format) {
                    case MP3:
                        newSongs.add(new MP3Song(owner, path, artist, title));
                        break;
                    case M4A:
                        newSongs.add(new M4ASong(owner, path, artist, title));
                        break;
                }
            }
            Source.LIBRARY.clear();
            Source.LIBRARY.add(newSongs);
        } catch (FileNotFoundException e) {
            Log.e(tag(this), "Library file does not exist", e);
        } catch (IOException e) {
            Log.e(tag(this), "Unable to read library file", e);
        } catch (NumberFormatException e) {
            Log.e(tag(this), "Malformed library file", e);
        } finally {
            try {
                if (reader != null) { reader.close(); }
            } catch (IOException e) {
                Log.e(tag(this), "Unable to close reader");
            }
        }
    }

    private final void loadLocalMusic() {
        Log.i(tag(this), "Loading music from SD card");

        for(final File file : FileUtils.listFiles(new File(EXTERNAL_PATH), EXTENSIONS, true)) {
            try {
                localSongs.add(Song.parse(device, file));
            } catch (Exception e) {
                Log.w(tag(this), format("Unable to parse song from: %s", file));
                continue;
            }
        }
        Source.LIBRARY.add(localSongs);
    }

    private class LibraryWriter extends Thread {
        @Override
        public final void run() {
            // If the library directory does not exist create it
            final File libraryDir = new File(LIBRARY_DIR);
            if (!libraryDir.isDirectory()) {
                if (libraryDir.mkdirs()) {
                    Log.i(tag(this), format("Created '%s'", libraryDir.getAbsolutePath()));
                }
            }

            BufferedWriter writer = null;
            try {
                // If the file does not exist create it
                if(!LIBRARY_FILE.exists()) {
                    if (LIBRARY_FILE.createNewFile()) {
                        Log.i(tag(this), format("Created '%s'", LIBRARY_FILE.getAbsolutePath()));
                    }
                }

                writer = new BufferedWriter(new FileWriter(LIBRARY_FILE, false));
                writer.write(format("%d\n", localSongs.size()));
                for (final Song song : localSongs) {
                    writer.write(format("%s\n", song.getPath()));
                    writer.write(format("%s\n", song.getFormat()));
                    writer.write(format("%s\n", song.getTitle()));
                    writer.write(format("%s\n", song.getArtist()));
                }

                Log.i(tag(this), format("Wrote %d song(s) to library file", localSongs.size()));
            } catch (IOException e) {
                Log.e(tag(this), "Unable to write library file", e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        Log.e(tag(this), "Unable to close file writer", e);
                    }
                }
            }
        }
    }
}
