package com.example.sockettest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class StreamSinkHelperThread extends Thread {
	static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	static final String WRITE_PATH = new String(SD_CARD_PATH + "/DELTA");
	static final String WRITE_FILE = new String("step");
    
	private ServerSocket server;
	private String address;
	private int port;
	private Socket sourceSocket;
	private Map<String,String> song;
	private InputStream inputStream;
	
	public StreamSinkHelperThread(Map<String,String> song, String address, int port) {
		this.address = address;
		this.port = port;
		this.song = song;
		Log.w("STREAMSINKHELPERTHREAD","STREAM MUSIC: " + this.song.get("title"));
		Log.w("STREAMSINKHELPERTHREAD","NEW STREAMSINKHELPERTHREAD");
	}
	
	public void run () {
		try {
			SocketAddress socketAddress = new InetSocketAddress(address, 5152);
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(socketAddress);
			
			File temp;
			
	        Log.w("STREAMSINKHELPERTHREAD","HOSTING AT: " + server.getLocalSocketAddress());
			
			while(true) {
		        sourceSocket = server.accept();
		        Log.w("STREAMSINKHELPERTHREAD","CONNECTED TO STREAMSOURCETHREAD");
		        
		        inputStream = sourceSocket.getInputStream();
		        
				byte[] sizeBuffer = new byte[1];
				byte[] audioBuffer;
				String accum = "";
				FileOutputStream fos;
				
				String ext = FilenameUtils.getExtension(song.get("path"));
				
				accum = "";
				inputStream.read(sizeBuffer);
				char cur = (char) sizeBuffer[0];
				
				while(cur != '\0') {
					accum += cur;
					inputStream.read(sizeBuffer);
					cur = (char) sizeBuffer[0];
				}
				
				JsonElement je = new JsonParser().parse(accum);
				
				JsonFrameDeserializer fgc = new JsonFrameDeserializer();
				int frameGroupCount = fgc.deserialize(je, null, null);
				
				Log.w("STREAMSINKHELPERTHREAD","FRAMEGROUPCOUNTER: " + frameGroupCount);
				
				for(int j = 0; j < frameGroupCount; j++) {
					accum = "";
					inputStream.read(sizeBuffer);
					char curr = (char) sizeBuffer[0];
					
					while(curr != '\0') {
						accum += curr;
						inputStream.read(sizeBuffer);
						curr = (char) sizeBuffer[0];
					}
					
					JsonElement jsonElement = new JsonParser().parse(accum);
									
					if(accum == "") {
						Log.w("STREAMSINKHELPERTHREAD","END OF STREAM INPUT");
						break;
					}
					
					JsonFrameDeserializer sizeDeserializer = new JsonFrameDeserializer();
					int size = sizeDeserializer.deserialize(jsonElement, null, null);
					
					temp = new File(WRITE_PATH, WRITE_FILE + j  + "." + ext);
			        temp.createNewFile();
			        
			        fos = new FileOutputStream(temp);
				
			        Log.w("STREAMSINKHELPERTHREAD","SIZE: " + size);
			        Log.w("STREAMSINKHELPERTHREAD","WRITING TO: " + temp.getName());
			        
			        audioBuffer = new byte[size];
			        
			        while(size > inputStream.available()) {
			        	try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        	Log.w("STREAMSINKHELPERTHREAD","WAITING FOR ENOUGH BYTES TO READ: " + inputStream.available() + " / " + size);
			        }
			        
			        inputStream.read(audioBuffer,0,size);
					fos.write(audioBuffer);
					
					fos.close();
					
					Log.w("STREAMSINKHELPERTHREAD", temp.getName() + " COMPLETE");
				}
				server.close();
				sourceSocket.close();
				Log.w("STREAMSINKHELPERTHREAD","STREAM COMPLETE; SERVER AND SOCKET CLOSED");
			}
		} catch (IOException e) {
			Log.w("STREAMSINKHELPERTHREAD","IO ERROR");
			Log.w("STREAMSINKHELPERTHREAD", e.getMessage());	
		} finally {
			if(server != null) {
				try {
					server.close();
					Log.w("STREAMSINKHELPERTHREAD","SERVER CLOSED");
				} catch (IOException e) {
					Log.w("STREAMSINKHELPERTHREAD","COULDN'T CLOSE SERVER");
					Log.w("STREAMSINKHELPERTHREAD", e.getMessage());
				}
			}
			if(sourceSocket != null) {
				try {
					sourceSocket.close();
					Log.w("STREAMSINKHELPERTHREAD","SOCKET CLOSED");
				} catch (IOException e) {
					Log.w("STREAMSINKHELPERTHREAD","COULDN'T CLOSE SERVER");
					Log.w("STREAMSINKHELPERTHREAD", e.getMessage());
				}
			}
		}
	}
}
