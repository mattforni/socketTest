package com.example.sockettest.ui.runnable;

import java.util.List;

import com.example.sockettest.Device;
import com.example.sockettest.music.Song;

public class UpdateLibraryRunnable implements Runnable {
	private final List<Song> library;
	private final Device device;
	
	public UpdateLibraryRunnable(List<Song> library, Device device) {
		this.library = library;
		this.device = device;
	}
	
    public void run() {
    	device.updateLibrary(library);
    }
}
