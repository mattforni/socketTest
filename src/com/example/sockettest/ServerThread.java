package com.example.sockettest;

import java.net.Socket;

public class ServerThread extends Thread {
	private String clientId;
	private Socket clientSocket;
	
    public ServerThread(String clientId, Socket clientSocket) {
        this.clientId = clientId;  
    	this.clientSocket = clientSocket;
    }
    public void run() {
         //(...other code here.)
    }
    
    public void updateMusicLibrary() {
    	
    }
    public void updateQueue() {
          //Signal to the thread that it needs to do something (which should then be handled in the run method)
    }
}
