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

import android.util.Log;

public class ServerThreadHandler extends Thread {
	
	private String address;
	private int port;
	private List<String> clientIdList;
	private Map<String,ServerThread> serverThreadsMap;
	private ServerSocket server;
	private Utilities utilities; 
	
	public ServerThreadHandler (String address, int port) {
		this.address = address;
		this.port = port;
		
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
          while (!isInterrupted()) { //You should handle interrupts in some way, so that the thread won't keep on forever if you exit the app.
        	  
        	  ServerThread newClientThread = null;
        	  Socket newClientSocket = null;
        	  String newClientId = utilities.getUUID();
        	  
        	  try {
        		  newClientSocket = server.accept();
        	  } catch (IOException e) {
        		  e.printStackTrace();
        	  }
        	  
    		  newClientThread = new ServerThread(newClientId, newClientSocket);
    		  
    		  
    		  serverThreadsMap.put(newClientId, newClientThread);
    		  clientIdList.add(newClientId);
    		  
    		  newClientThread.start();
          }
    }

    public void updateQueueAllThreads() {
    	for(String id : clientIdList) {
    		serverThreadsMap.get(id).updateQueue();
    	}
    }
    
    public void updateMusicLibraryAllThreads() {
    	for(String id : clientIdList) { 
    		serverThreadsMap.get(id).updateMusicLibrary();
        }
    }
}
