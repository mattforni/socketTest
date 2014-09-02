package com.example.sockettest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.os.Handler;
import android.util.Log;

public class ServerThreadHandler extends Thread {
	
	private String address;
	private int port;
	private List<String> clientIdList;
	private Map<String,ServerThread> serverThreadsMap;
	private ServerSocket server;
	private Utilities utilities; 
	private List<Map<String,String>> ServerLibrary;
	private Handler updateUIHandler;
	private Handler playlistHandler;
	private Handler disconnectHandler;
	private String lastAddedId;
	private Queue<Map<String,String>> playlistViewList;
	
	public ServerThreadHandler (String address, int port, Handler updateUIHandler, Handler playlistHandler, Handler disconnectHandler, List<Map<String,String>> ServerLibrary, Queue<Map<String,String>> playlistViewList) {
		this.address = address;
		this.port = port;
		this.updateUIHandler = updateUIHandler;
		this.ServerLibrary = ServerLibrary;
		this.playlistHandler = playlistHandler;
		this.playlistViewList = playlistViewList;
		this.disconnectHandler = disconnectHandler;
		
		clientIdList = new LinkedList<String>();
		serverThreadsMap = new HashMap<String,ServerThread>();
		utilities = new Utilities();
		
		SocketAddress socketAddress = new InetSocketAddress(this.address, this.port);
		SocketAddress defaultSocketAddress = new InetSocketAddress("0.0.0.0" , 8080);
		
		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(socketAddress);
	        Log.w("SERVERTHREADHANDLER","HOSTING AT: " + server.getLocalSocketAddress());
			
		} catch (IOException e) {
			Log.e("HOST","CANNOT INITIALIZE SERVERSOCKET");
			Log.e("HOST",e.getMessage());
			try {
				Log.w("HOST","INITIALIZING SOCKET WITH DEFAULT ADDRESS");
				server = new ServerSocket();
				server.bind(defaultSocketAddress);
			} catch (IOException e1) {
				Log.e("HOST","CANNOT INITIALIZE SERVERSOCKET WITH DEFAULT ADDRESS");
				Log.e("HOST",e1.getMessage());
			}
		}
	}
	
    public void run() {
    	Log.w("SERVERTHREADHANDLER","RUNNING");
          while (!isInterrupted()) { // You should handle interrupts in some way, so that the thread won't keep on forever if you exit the app.
        	  
        	  ServerThread newClientThread = null;
        	  Socket newClientSocket = null;
        	  String newClientId = utilities.getUUID();
        	  
        	  try {
        		  newClientSocket = server.accept();
        		  Log.w("SERVERTHREADHANDLER","ACCEPTING; ID : " + newClientId);
        	  } catch (IOException e) {
        		  e.printStackTrace();
        	  }
        	  
    		  newClientThread = new ServerThread(newClientId, playlistViewList, newClientSocket,updateUIHandler,playlistHandler, disconnectHandler);
    		  
    		  serverThreadsMap.put(newClientId, newClientThread);
    		  clientIdList.add(newClientId);
    		  lastAddedId = newClientId;

    		  newClientThread.start();
          }
    }
    
    public void updateplaylistAllThreads(Map<String,String> song) {
    	for(String id : clientIdList) {
    		Log.w("SERVERTHREADHANDLER","UPDATING PLAYLIST FOR ID: " + id);
    		serverThreadsMap.get(id).updatePlaylist(song);
    	}
    }
    
    public void updateMusicLibraryAllThreads() {
    	Log.w("SERVERHANDLERTHREAD","UPDATE MUSIC LIBRARY FOR ALL THREADS");
    	for(String id : clientIdList) {
    		Log.w("SERVERTHREADHANDLER","UPDATING LIBRARY FOR ID: " + id);
    		serverThreadsMap.get(id).updateMusicLibrary(ServerLibrary);
        }
    }
    public String getLastAdded() {
    	return lastAddedId;
    }
    
    public List<Map<String,String>> getNewMusic(String id) {
    	return serverThreadsMap.get(id).getLocalLibrary();
    }
    
    public ServerThread getServerThread(String id) {
    	return serverThreadsMap.get(id);
    }
    
    public void updateCurrentSongAllThreads(Map<String,String> song) {
    	for(String id : clientIdList) {
    		Log.w("SERVERTHREADHANDLER","UPDATING CURRENT SONG FOR ID: " + id);
    		serverThreadsMap.get(id).updateCurrentSong(song);
        }
    }
    public List<Map<String,String>> getEnqueuedSongsAllThreads() {
    	List<Map<String,String>> list = new LinkedList<Map<String,String>>();
    	for(String id : clientIdList) {
    		Map<String,String> song = serverThreadsMap.get(id).getEnqeuedSong();
    		if(song != null) {
    			Log.w("SERVERTHREADHANDLER","ENQUEUE: " + serverThreadsMap.get(id).getEnqeuedSong());
    			list.add(song);
    		}
        }
    	return list;
    }

	public void dequeueAllThreads() {
		for(String id : clientIdList) {
			Log.w("SERVERTHREADHANDLER","DEQUEUING SONG FOR ID: " + id);
    		serverThreadsMap.get(id).dequeue();
        }	
	}
	
	public String getDisconnectingThreadId() {
		for(String id : clientIdList) {
			if(serverThreadsMap.get(id).isDisconnecting()) {
				return id;
			}
		}
		return null;
	}
	
	public void removeThread(String id) {
		Log.w("SERVERTHREADHANDLER","REMOVING ID: " + id);
		clientIdList.remove(id);
		serverThreadsMap.remove(id);
	}
}
