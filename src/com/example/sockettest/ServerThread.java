package com.example.sockettest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ServerThread extends Thread {
	private String clientId;
	private Socket clientSocket;
	private byte[] buffer;
	private InputStream inputStream;
	private OutputStream outputStream;
	private List<Map<String, String>> localMusicList;
	private Handler updateUIHandler;
	private Handler playlistHandler;
	private Map<String,String> enqueuedSong;
	private boolean disconnecting;
	private boolean initialized;
	private Handler disconnectHandler;
	private Queue<Map<String,String>> playlistViewList;
	
    public ServerThread(String clientId, Queue<Map<String,String>> playlistViewList, Socket clientSocket, Handler updateUIHandler, Handler playlistHandler, Handler disconnectHandler) {
        this.clientId = clientId;  
    	this.clientSocket = clientSocket;
    	this.playlistHandler = playlistHandler;
    	localMusicList = new LinkedList<Map<String,String>>();
    	this.updateUIHandler = updateUIHandler;
    	this.disconnectHandler = disconnectHandler;
    	this.playlistViewList = playlistViewList;
    	inputStream = null;
    	buffer = new byte[1];
    	enqueuedSong = new HashMap<String,String>();
    	disconnecting = false;
    	initialized = false;
    }
    public void run() {
    	try {
    		
    		//////////	  	SEND ID			//////////
    		
	    	JsonIdSerializer serializerId = new JsonIdSerializer();
	    	JsonElement serializedId = serializerId.serialize(clientId, null, null);
	
			outputStream = clientSocket.getOutputStream();
			
			outputStream.write(0);
			outputStream.write(serializedId.toString().getBytes());
			outputStream.write('\0');
    	
    		inputStream = clientSocket.getInputStream();
    		
	    	while(!disconnecting)
			{
				inputStream.read(buffer);
				byte code = buffer[0];
				
				String accum = "";
				char curr;
				
				switch(code) {
					case 0:
						
						if(initialized) {
							disconnecting = true;
							clientSocket.close();
							Message msg1 = Message.obtain();
							disconnectHandler.sendMessage(msg1);
							break;
						}
						
						//////////	  RECIEVE MUSIC		//////////
						
						Log.w("SERVERTHREAD","CODE 0 RECIEVED");
						inputStream.read(buffer);
						curr = (char) buffer[0];
						
						while(curr != '\0') {
							if(curr == '\n') {
								JsonElement jsonElement = new JsonParser().parse(accum);
								
								JsonSongDeserializer deserializer = new JsonSongDeserializer();
								Map<String,String> song = deserializer.deserialize(jsonElement, null, null);
								
								localMusicList.add(song);

								accum = "";
							}
							else {
								accum += curr;
							}
							
							inputStream.read(buffer);
							curr = (char) buffer[0];
						}
						Message msg = Message.obtain();
						updateUIHandler.sendMessage(msg);
						initialized = true;
						break;
					case 1:
						inputStream.read(buffer);
						curr = (char) buffer[0];
														
						while(curr != '\0') {
							accum += curr;
							inputStream.read(buffer);
							curr = (char) buffer[0];
						}
	
						JsonSongDeserializer deserializer = new JsonSongDeserializer();
						JsonElement jsonElement = new JsonParser().parse(accum);

						enqueuedSong = deserializer.deserialize(jsonElement, null, null);
						Message msg2 = Message.obtain();
						playlistHandler.sendMessage(msg2);
						break;
					case 5:
						Log.w("SERVERTHREAD","CODE 5 RECIEVED");
						disconnecting = true;
						clientSocket.close();
						Message msg1 = Message.obtain();
						disconnectHandler.sendMessage(msg1);
						break;
				}
			}
    	}
    	catch(IOException e)
    	{
    		Log.e("SERVERTHREAD", "COULDN'T SEND DATA");
			Log.e("SERVERTHREAD", e.toString());
    	}
    }
    
    public List<Map<String,String>> getLocalLibrary() {
    	return localMusicList;
    }
    
    public void updateMusicLibrary(List<Map<String,String>> newMusicList) {
    	JsonSongSerializer serializerSong = new JsonSongSerializer();
		
    	try {
    		outputStream.write(0);
			for(Map<String,String> song : newMusicList) {
				JsonElement serializedSong = serializerSong.serialize(song, null, null);
				outputStream.write(serializedSong.toString().getBytes());
				outputStream.write(10);
		    	
			}
			outputStream.write('\0');
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","IO ERROR");
    	}
    }
    
    public Map<String,String> getEnqeuedSong() {
    	if(enqueuedSong != null)
    	{
    		Log.w("SERVERTHREAD","ENQUEUED: " + enqueuedSong.get("title"));
	    	Map<String,String> temp = new HashMap<String,String>();
	    	temp.put("ownerId",enqueuedSong.get("ownerId"));
	    	temp.put("title",enqueuedSong.get("title"));
	    	temp.put("artist",enqueuedSong.get("artist"));
	    	temp.put("path",enqueuedSong.get("path"));
	    	enqueuedSong = null;
	    	return temp;
    	}
    	else {
    		return null;
    	}
    }
    
    public void initializePlaylist() {
    	for(Map<String,String> song : playlistViewList) {
    		Log.w("SERVERTHREAD","ENQUEUING: " + song.get("title"));
    		updatePlaylist(song);
    	}
    }
    
    
    public void updatePlaylist(Map<String,String> song) {
    	Log.w("SERVERTHREAD","UPDATING PLAYLIST");
		JsonSongSerializer serializerSong = new JsonSongSerializer();
		
		try {
    		outputStream.write(1);
			JsonElement serializedSong = serializerSong.serialize(song, null, null);
			outputStream.write(serializedSong.toString().getBytes());
			outputStream.write('\0');
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","IO ERROR");
    	}
    }
    
	public void streamMusic(Map<String,String> song) {
		JsonSongSerializer serializerSong = new JsonSongSerializer();
		
		try {
    		outputStream.write(3);
			JsonElement serializedSong = serializerSong.serialize(song, null, null);
			outputStream.write(serializedSong.toString().getBytes());
			outputStream.write('\0');
			
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","IO ERROR");
    	}
	}
	
	public void updateCurrentSong(Map<String, String> song) {
		Log.w("SERVERTHREAD","UPDATING CURRENT SONG PLAYING");
		JsonSongSerializer serializerSong = new JsonSongSerializer();
		
		try {
    		outputStream.write(2);
			JsonElement serializedSong = serializerSong.serialize(song, null, null);
			outputStream.write(serializedSong.toString().getBytes());
			outputStream.write('\0');
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","IO ERROR");
    	}
	}
	public Socket getSocket() {
		return clientSocket;
	}
	public void dequeue() {
		Log.w("SERVERTHREAD","DEQUEUE");
		
		try {
    		outputStream.write(4);
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","IO ERROR");
    	}
	}
	public boolean isDisconnecting() {
		return disconnecting;
	}
}
