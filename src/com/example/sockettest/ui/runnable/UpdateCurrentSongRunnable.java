package com.example.sockettest.ui.runnable;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;

public class UpdateCurrentSongRunnable implements Runnable {
	private final Song song;
	private final Device device;
	
	public UpdateCurrentSongRunnable(Song song, Device device) {
		this.song = song;
		this.device = device;
	}
	
    public void run() {
    	device.updateCurrentSong(song);
    }
}
