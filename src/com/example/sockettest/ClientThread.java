package com.example.sockettest;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.util.Log;

public class ClientThread extends Thread {
	private String address;
	private int port;
	private Socket socket;
	private String id;
	private byte[] codeBuffer;
	private byte[] dataBuffer;
	
	public ClientThread(String address, int port){
		this.address = address;
		this.port = port;
		socket = null;
		codeBuffer = new byte[1];
		dataBuffer = new byte[1024];
	}
	
	public void run() {
		try {
			socket = new Socket(address, port);
			
			InputStream inputStream = socket.getInputStream();
			
			inputStream.read(codeBuffer);
			Log.w("CLIENTTHREAD","READ CODE: " + codeBuffer[0]);
			
			inputStream.read(dataBuffer);
			id = new String(dataBuffer, "UTF-8");
			Log.w("CLIENTTHREAD","CLIENT ID: " + id);
			
			/*
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1){
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
			*/
			
			Log.w("CLIENTTHREAD","JOINED : " + socket.getLocalSocketAddress());

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
}