package com.example.sockettest;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Comparator;
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
	public Comparator<Map<String,String>> songComparator;
	
	public Utilities() {
		songComparator = new Comparator<Map<String,String>>() {
			@Override
			public int compare(Map<String, String> lhs, Map<String, String> rhs) {
				if(lhs.get("title") == null) {
					return 1;
				}
				else if (rhs.get("title") == null)
				{
					return -1;
				}
				return lhs.get("title").compareTo(rhs.get("title"));
			}
		};
	}
	
	public List<Map<String,String>> copyList(List<Map<String,String>> list) {
		List<Map<String,String>> returnList;
		returnList = new LinkedList<Map<String,String>>();
		for(Map<String,String> song : list) {
			returnList.add(song);
		}
		return returnList;
		
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
	
	public Map<Integer,Map<String,String>> addLabels(List<Map<String,String>> list) {
		Map<Integer,Map<String,String>> ret = new HashMap<Integer,Map<String,String>>();
		Map<String,String> label;
		
		label = new HashMap<String,String>();
		label.put("title", "#");
		label.put("artist", "");
		label.put("ownerId", "UI");
		
		char cur = 'A';
		
		
		int i = 0;
		
		ret.put(i, label);
		
		while(!(list.get(i).get("title").charAt(0) > 'A' && list.get(i).get("title").charAt(0) < 'Z')) {
			Log.w("UTILITIES", "I: " + i);
			i++;
		}
		
		while(cur <= 'Z') {
			label = new HashMap<String,String>();
			label.put("title", ""+cur);
			label.put("artist", "");
			label.put("ownerId", "UI");
			
			while(i < list.size() && list.get(i).get("title").charAt(0) == cur) {
				i++;
			}
			if(!ret.containsKey(i)) {
				ret.put(i, label);
			}
			
			
			Log.w("UTILITIES", "I: " + i + "; LABEL: " + cur);
			
			cur++;
		}
		
		return ret;
		
	}
	
	public void removeLabels() {
		
	}
}
