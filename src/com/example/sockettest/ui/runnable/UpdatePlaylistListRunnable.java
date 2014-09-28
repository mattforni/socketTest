package com.example.sockettest.ui.runnable;

import java.util.List;
import java.util.Map;

import android.widget.SimpleAdapter;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;
import com.example.sockettest.utils.Songs;

public class UpdatePlaylistListRunnable implements Runnable {
	private final List<Song> playlist;
	private final List<Map<String,String>> playlistList;
	private final SimpleAdapter playlistAdapter;
	
	public UpdatePlaylistListRunnable(List<Song> playlist, List<Map<String,String>> playlistList, SimpleAdapter playlistAdapter) {
		this.playlist = playlist;
		this.playlistList = playlistList;
		this.playlistAdapter = playlistAdapter;
	}
	
    public void run() {
    	playlistList.clear();
    	playlistList.addAll(Songs.toListOfMaps(playlist));
    	playlistAdapter.notifyDataSetChanged();
    }
}