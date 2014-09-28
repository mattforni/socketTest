package com.example.sockettest.ui.runnable;

import java.util.List;
import java.util.Map;

import android.widget.SimpleAdapter;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.example.sockettest.utils.Songs;

public class UpdateLibraryListRunnable implements Runnable {
	private final List<Song> library;
	private final List<Map<String,String>> libraryList;
	private final SimpleAdapter libraryAdapter;
	
	public UpdateLibraryListRunnable(List<Song> library, List<Map<String,String>> libraryList, SimpleAdapter libraryAdapter) {
		this.library = library;
		this.libraryList = libraryList;
		this.libraryAdapter = libraryAdapter;
	}
	
    public void run() {
    	libraryList.clear();
    	libraryList.addAll(Songs.toListOfMaps(library));
    	libraryAdapter.notifyDataSetChanged();
    }
}