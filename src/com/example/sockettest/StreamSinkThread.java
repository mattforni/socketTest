package com.example.sockettest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class StreamSinkThread extends Thread {
	static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	static final String READ_PATH = new String(SD_CARD_PATH + "/DELTA");
	static final String WRITE_FILE = new String("step");
    
	private Handler onStreamCompletionHandler;
	private String address;
	private int port;
	private File frameGroup;
	private File nextFrameGroup;
	private int i;
	private MediaPlayer mediaPlayer;
	private FileInputStream fis;
	private Map<String, String> song;
	private boolean play;
	
	public StreamSinkThread(Map<String,String> song, Handler onStreamCompletionHandler, String address, int port) {
		this.onStreamCompletionHandler = onStreamCompletionHandler;
		this.address = address;
		this.port = port;
		this.mediaPlayer = new MediaPlayer();
		this.song = song;
		this.play = true;
		i = 0;
		
		new StreamSinkHelperThread(this.song, this.address, this.port).start();
		Log.w("STREAMSINKTHREAD","NEW STREAMSINKTHREAD");
	}

	public void run () {
		Log.w("STREAMSINKTHREAD","STARTING STREAM");
		String ext = FilenameUtils.getExtension(song.get("path"));
		frameGroup = new File(READ_PATH, WRITE_FILE + i  + "." +  ext);
		nextFrameGroup = new File(READ_PATH, WRITE_FILE + (i + 1)  + "." +  ext);
		
		while(!nextFrameGroup.exists()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.w("STREAMSINKTHREAD", "WAITING FOR: " + nextFrameGroup.getName());
		}
		
		Log.w("STREAMSINKTHREAD","PLAYING STREAM");
		
		do {
			nextFrameGroup = new File(READ_PATH, song.get("title") + (i + 1)  + "." +  ext);
			
			mediaPlayer.reset();
			
			try {
				fis = new FileInputStream(frameGroup);
				mediaPlayer.setDataSource(fis.getFD());
				mediaPlayer.prepare();
				
			} catch (FileNotFoundException e) {
				Log.w("STREAMSINKTHREAD","FILE NOT FOUND");
				Log.w("STREAMSINKTHREAD",e.getMessage());
			} catch (IllegalArgumentException e) {
				Log.w("STREAMSINKTHREAD","ILLEGAL ARGUMENT");
				Log.w("STREAMSINKTHREAD",e.getMessage());
			} catch (IllegalStateException e) {
				Log.w("STREAMSINKTHREAD","ILLEGAL STATE");
				Log.w("STREAMSINKTHREAD",e.getMessage());
			} catch (IOException e) {
				Log.w("STREAMSINKTHREAD","IO ERROR");
				Log.w("STREAMSINKTHREAD",e.getMessage());
			}
			
			Log.w("STREAMSINKTHREAD","PLAYING STREAM");
			mediaPlayer.start();
			
			while(true) {
				if(!mediaPlayer.isPlaying()) {
					Log.w("STREAMSINKTHREAD", "DELETING " + frameGroup.getName());
					frameGroup.delete();
					break;
				}
				while (!play) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			i++;
			frameGroup = new File(READ_PATH, WRITE_FILE + i + "." + ext);
			Log.w("STREAMSINKTHREAD","PLAY: " + play + "; EXISTS: " + frameGroup.exists());
		} while(frameGroup.exists() && play);
		
		if (play) {
			Log.w("STREAMSINKTHREAD","SENDING MESSAGE TO HANDLER");
			Message msg = Message.obtain();
			onStreamCompletionHandler.sendMessage(msg);
		}
	}
	
	public void pause() {
		play = false;
		mediaPlayer.pause();
	}
	
	public void play() {
		play = true;
		mediaPlayer.start();
	}
	
	public void flush() {
		Log.w("STREAMSINKTHREAD","FLUSHING STREAM");
		File writeFolder = new File(READ_PATH);
    	String[] extensions = new String[]{"mp3","MP3","m4a","M4A"};
    	List<File> mediaFiles = (List<File>)FileUtils.listFiles(writeFolder, extensions, true);
    	
    	for(File f : mediaFiles) {
    		Log.w("STREAMSINKTHREAD","DELETING: " + f.getName());
    		f.delete();
    	}
	}
}

