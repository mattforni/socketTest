package com.example.sockettest.ui.runnable;

import android.widget.TextView;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.music.Song;

public class UpdateCurrentSongRunnable implements Runnable {
	private final Song song;
	private final Device device;
	private final TextView currentTitle, currentArtist;
	
	public UpdateCurrentSongRunnable(Song song, Device device) {
		this.song = song;
		this.device = device;
		this.currentTitle = (TextView) device.findViewById(R.id.current_title);
		this.currentArtist = (TextView) device.findViewById(R.id.current_artist);
	}
	
    public void run() {
        currentArtist.setText(song.getArtist());
        currentTitle.setText(song.getTitle());

        currentArtist.setSelected(true);
        currentTitle.setSelected(true);
    }
}
