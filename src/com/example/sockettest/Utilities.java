package com.example.sockettest;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import android.os.Environment;
import android.util.Log;

public class Utilities {
	public static byte INITIALIZATION = 0;
	public static byte REQUEST_SONG = 1;
	public static byte STREAMING = 2;
	
	public Utilities() {
		
	}
		
	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
			
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
				
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();
					
					if (inetAddress.isSiteLocalAddress()) {
						ip = inetAddress.getHostAddress();
					}
				}
			}   
		} catch (SocketException e) {
			Log.e("UTILITIES", "COULDN'T RETRIEVE IP");
			Log.e("UTILITIES", e.getMessage());
		}
		
		Log.w("UTILITIES","IP: " + ip);
		return ip;
	}
	
	
	
	public String getMusicFiles() {
    	String files = "";		
		List<File> filesList = (List<File>)FileUtils.listFiles(new File(Environment.getExternalStorageDirectory().toString()), new String[]{"mp3","MP3"}, true);
		
    	for(File f : filesList) {
    		files = files + f.getPath() + "\n";
    	}
    	
    	return files;
	}
	
	public String getUUID() {
		return UUID.randomUUID().toString();
	}
}
