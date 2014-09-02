package com.example.sockettest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ClientThread extends Thread {
	private String address;
	private int port;
	private Socket socket;
	private String id;
	private byte[] buffer;
	private List<Map<String,String>> libraryViewList;
	private Handler musicLibraryHandler;
	private Handler recieveIdHandler;
	private Handler UIHandler;
	private Handler playlistHandler;
	private Handler dequeuePlaylistHandler;
	private List<Map<String,String>> localMusicList;
	private Map<String,String> currentSong;
	private InputStream inputStream;
	private List<Map<String, String>> enqueuedSongs;
	
	public ClientThread(String address, int port, List<Map<String,String>> libraryViewList, Handler musicLibraryHandler, Handler recieveIdHandler, Handler UIHandler, Handler playlistHandler, Handler dequeuePlaylistHandler){
		this.address = address;
		this.port = port;
		this.socket = null;
		this.buffer = new byte[1];
		this.currentSong = new HashMap<String,String>();
		this.enqueuedSongs = new LinkedList<Map<String,String>>();
		this.recieveIdHandler = recieveIdHandler;
		this.musicLibraryHandler = musicLibraryHandler;
		this.UIHandler = UIHandler;
		this.playlistHandler = playlistHandler;
		this.dequeuePlaylistHandler = dequeuePlaylistHandler;
		this.libraryViewList = libraryViewList;
		localMusicList = new LinkedList<Map<String,String>>();  
	}
	
	public void run() {
		try {
			socket = new Socket(address, port);
			Log.w("CLIENTTHREAD","JOINED : " + socket.getLocalSocketAddress());
			
			inputStream = socket.getInputStream();
			
			byte code;
			
			while(!isInterrupted()) {
				inputStream.read(buffer);
				code = buffer[0];
				
				char curr;
				String accum = "";
				
				Log.w("CLIENTTHREAD","CODE: " + code);
				
				switch(code) {
					case 0:
						
						//////////		RECIEVE ID			//////////
						
						Log.w("CLIENTTHREAD","CODE 0 RECIEVED");
						inputStream.read(buffer);
						curr = (char) buffer[0];
							
						while(curr != '\0') {
							accum += curr;
								
							inputStream.read(buffer);
							curr = (char) buffer[0];
						}
						Log.w("CLIENTTHREAD","READ: " + accum);
							
						JsonElement jsonElement = new JsonParser().parse(accum);
						JsonIdDeserializer deserializerId = new JsonIdDeserializer();
						id = deserializerId.deserialize(jsonElement, null, null);
							
						Log.w("CLIENTTHREAD","ID RECIEVED: " + id);
						Message msg = Message.obtain();
						recieveIdHandler.sendMessage(msg);
						
						Thread.sleep(1500);
						
						//////////		SEND MUSIC		//////////
						
						JsonSongSerializer serializerSong = new JsonSongSerializer();
						
						OutputStream outputStream = socket.getOutputStream();
			    		outputStream.write(0);
						
			    		for(Map<String,String> song : libraryViewList) {
							JsonElement serializedSong = serializerSong.serialize(song, null, null);
					    	Log.w("CLIENTTHREAD", "WRITING: " + serializedSong.toString());
								outputStream.write(serializedSong.toString().getBytes());
								outputStream.write(10);
						}
						outputStream.write('\0');
						
						//////////	  RECIEVE MUSIC		//////////
						
						accum = "";

						Thread.sleep(1500);
						inputStream.read(buffer);
						inputStream.read(buffer);
						curr = (char) buffer[0];
						
						while(curr != '\0') {
							if(curr == '\n') {
								Log.w("CLIENTTHREAD","READ: " + accum);
								
								jsonElement = new JsonParser().parse(accum);
								
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
						Message msg1 = Message.obtain();
						musicLibraryHandler.sendMessage(msg1);
						Log.w("CLIENTTHREAD","ALL DATA RECIEVED");
						break;
						
					case 1:
						Log.w("CLIENTTHREAD","CODE 1 RECIEVED");
						
						inputStream.read(buffer);
						curr = (char) buffer[0];
														
						while(curr != '\0') {
							accum += curr;
							inputStream.read(buffer);
							curr = (char) buffer[0];
						}
						
						Log.w("CLIENTTHREAD","READ 1: " + accum);
						
						jsonElement = new JsonParser().parse(accum);
																
						JsonSongDeserializer songDeserializer = new JsonSongDeserializer();
						enqueuedSongs.add(songDeserializer.deserialize(jsonElement, null, null));
						
						Message msg2 = Message.obtain();
						playlistHandler.sendMessage(msg2);
						break;
						
					case 2:
						Log.w("CLIENTTHREAD","CODE 2 RECIEVED");
						
						//////////		  RECIEVE MUSIC		//////////
						
						inputStream.read(buffer);
						curr = (char) buffer[0];
														
						while(curr != '\0') {
							accum += curr;
							inputStream.read(buffer);
							curr = (char) buffer[0];
						}
						Log.w("CLIENTTHREAD","READ: " + accum);
						
						jsonElement = new JsonParser().parse(accum);
																
						songDeserializer = new JsonSongDeserializer();
						currentSong = songDeserializer.deserialize(jsonElement, null, null);
						
						Message msg3 = Message.obtain();
						UIHandler.sendMessage(msg3);
						break;
					case 3:
						Log.w("CLIENTTHREAD","CODE 3 RECIEVED");
						
						//////////		  RECIEVE MUSIC		//////////
						
						inputStream.read(buffer);
						curr = (char) buffer[0];
														
						while(curr != '\0') {
							accum += curr;
							inputStream.read(buffer);
							curr = (char) buffer[0];
						}
						Log.w("CLIENTTHREAD","READ: " + accum);
						
						jsonElement = new JsonParser().parse(accum);
																
						songDeserializer = new JsonSongDeserializer();
						Map<String,String> streamSong = songDeserializer.deserialize(jsonElement, null, null);
						streamSong(streamSong);
						break;
					case 4:
						Message msg4 = Message.obtain();
						dequeuePlaylistHandler.sendMessage(msg4);
						break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void setMusicLibrary(List<Map<String,String>> list) {
		libraryViewList = list;
	}
	
	public String getClientId() {
		return id;
	}
	
	public void enqueue(Map<String,String> song) {
		JsonSongSerializer serializer = new JsonSongSerializer();
		
    	try {
    		OutputStream outputStream = socket.getOutputStream();
    		
    		outputStream.write(1);	
			JsonElement serializedSong = serializer.serialize(song, null, null);
			outputStream.write(serializedSong.toString().getBytes());
			outputStream.write('\0');
			
			Log.w("CLIENTTHREAD", serializedSong.toString());
			
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","CANNOT ENQUEUE SONG");
    		Log.e("CLIENTTHREAD",e.getMessage());
    	}
	}
	
	public List<Map<String,String>> getNewMusic() {
		return localMusicList;
	}
	
	public Map<String,String> getCurrentSong() {
		return currentSong;
	}
	
	public List<Map<String,String>> getEnqueuedSongs() {
		List<Map<String,String>> temp = enqueuedSongs;
		enqueuedSongs = new LinkedList<Map<String,String>>();
		return temp;
	}
	
	public void streamSong(Map<String,String> song) {
		Log.w("CLIENTTHREAD","STREAMING SONG");
 
		StreamSourceThread sourceThread = new StreamSourceThread(song.get("path"), port, address);
		sourceThread.start();
	}
	
	public void disconnect() {
		Log.w("CLIENTTHREAD","DISCONNECTING FROM HOST");

		try {
    		OutputStream outputStream = socket.getOutputStream();
    		
    		outputStream.write(5);
			
		} catch(IOException e) {
    		Log.e("CLIENTTHREAD","CANNOT PROPERLY DISCONNECT FROM HOST");
    		Log.e("CLIENTTHREAD",e.getMessage());
    	}
	}
}