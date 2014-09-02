package com.example.sockettest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.JsonElement;

public class StreamSourceThread extends Thread{
	private Socket socket;
	private String address;
	private int port;
	private String path;
	private File temp;
	
	final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
    final String MEDIA_PATH = new String(SD_CARD_PATH);
    final String WRITE_PATH = new String(SD_CARD_PATH + "/DELTA");
    final String WRITE_FILE = new String("SPLIT");
    final int STEP_SIZE = 600;
	
	public StreamSourceThread(String path, int port, String address) {
		this.path = path;
	    this.address = address;
	    this.port = port;
	    Log.w("STREAMSOURCETHREAD","CONSTRUCTOR");
		
	}
	
	public void run() {
		try {
			socket = new Socket(address, 5152);
			socket.setReuseAddress(true);
			
			CheapSoundFile reader = CheapSoundFile.create(path);
			OutputStream outputStream = socket.getOutputStream();
			byte[] filesToBytes = null;
			Integer size;
			JsonFrameSerializer sizeSerialier;
			JsonElement elem;
			
			String ext = FilenameUtils.getExtension(path);
			
			temp  = new File(WRITE_PATH, WRITE_FILE + ext);
			
			int frameGroupCount = reader.getNumFrames() / STEP_SIZE;

			if(reader.getNumFrames() % STEP_SIZE > 0) {
				frameGroupCount++;
			}
			
			Log.w("STREAMSOURCETHREAD","FRAMEGROUPCOUNT: " + frameGroupCount);
			
			sizeSerialier = new JsonFrameSerializer();
			elem = sizeSerialier.serialize(frameGroupCount, null, null);
			
			outputStream.write(elem.toString().getBytes());
			outputStream.write('\0');
			
			for(int stepStart = 0; stepStart < reader.getNumFrames(); stepStart += STEP_SIZE) {
				if(ext.equals("mp3") || ext.equals("MP3")) {
					((CheapMP3) reader).WriteFile(temp, stepStart, STEP_SIZE);
				}
				else if(ext.equals("m4a") || ext.equals("M4A")) {
					((CheapAAC) reader).WriteFile(temp, stepStart, STEP_SIZE);
				}
				
				InputStream input = new FileInputStream(temp);
				filesToBytes = IOUtils.toByteArray(input);
				
				size = new Integer(filesToBytes.length);
				
				Log.w("STREAMSOURCETHREAD","AT: " + stepStart + " TO: " + (stepStart + STEP_SIZE) + " OF: " + reader.getNumFrames() + "; READING: " + size + " BYTES");
				
				sizeSerialier = new JsonFrameSerializer();
				elem = sizeSerialier.serialize(size, null, null);

				// SEND SIZE OF BUFFER
			
				outputStream.write(elem.toString().getBytes());
				outputStream.write('\0');
				outputStream.write(filesToBytes);
				
				temp.delete();
			}
			
			sizeSerialier = new JsonFrameSerializer();
			elem = sizeSerialier.serialize(new Integer(0), null, null);
			outputStream.write('\0');
		} catch (FileNotFoundException e) {
			Log.w("STREAMSOURCETHREAD","FILE NOT FOUND");
			Log.w("STREAMSOURCETHREAD", e.getMessage());
		} catch (IOException e) {
			Log.w("STREAMSOURCETHREAD","IO EXCEPTION");
			Log.w("STREAMSOURCETHREAD", e.getMessage());
		} finally {
			try {
				if (socket != null) {
					socket.close();
					Log.w("STREAMSOURCETHREAD","SOCKET CLOSED");
				}
				if (temp != null) {
					temp.delete();
				}
				Log.w("STREAMSOURCETHREAD","EXTING THREAD");
			} catch (IOException e) {
				Log.w("STREAMSOURCETHREAD","COULDN'T CLOSE SOCKET");
				Log.w("STREAMSOURCETHREAD", e.getMessage());
			}
		}
	}
}
