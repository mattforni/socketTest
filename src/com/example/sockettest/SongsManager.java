package com.example.sockettest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Environment;
import android.util.Log;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class SongsManager {
    // SDCard Path
    //final String MEDIA_PATH = new String("/Music");
    final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
    final String MEDIA_PATH = new String(SD_CARD_PATH);
    final String WRITE_PATH = new String(SD_CARD_PATH + "/DELTA");
    final String WRITE_FILE = new String("MusicList.txt");

    private List<Map<String, String>> songsList = new LinkedList<Map<String, String>>();
    private List<String> artworkList = new LinkedList<String>();
    private File writeFolder;
    private File writeFile;
    private File mediaFolder;
    private List<File> mediaFiles;
    private String id;
    private String[] extensions;
    
    // Constructor
    public SongsManager(String id){
    	writeFolder = new File(WRITE_PATH);
    	mediaFolder = new File(MEDIA_PATH);
    	writeFile = new File(writeFolder, WRITE_FILE); 
    	extensions = new String[]{"mp3","MP3","m4a","M4A"};
    	mediaFiles = (List<File>)FileUtils.listFiles(mediaFolder, extensions, true);
    	this.id = id;
    	
        if (!writeFolder.exists()) {
        	writeFolder.mkdir();
        }
        if(!writeFile.exists()) {
        	try {
				writeFile.createNewFile();
				initializeWriteFile();
			} catch (IOException e) {
				Log.e("SONGMANAGER","COULDN'T WRITE MEDIA FILE");
				Log.e("SONGMANAGER", e.getMessage());
			}
        }
        updateMusicList();
    	loadMusicList();
    }
    
    public List<Map<String,String>> getListViewList() {
    	return songsList;
    }

    public List<String> getArtworkList() {
    	return artworkList;
    }
    
    public void initializeWriteFile() {
		FileWriter writer;
		try {
			writer = new FileWriter(writeFile);
		    writer.write("0\n");
			writer.close();
		} catch (IOException e) {
			Log.e("SONGMANAGER","COULDN'T INITIALIZE MEDIA FILE");
			Log.e("SONGMANAGER", e.getMessage());
		}
    }
    
    public void updateMusicList() {
    	File tmp = new File(WRITE_PATH, "tmp.txt");
		try {
			tmp.createNewFile();
	    	updateOldMusic();
			updateNewMusic();
	    	writeFile.delete();
	    	tmp.renameTo(writeFile);
		} catch (IOException e) {
			Log.e("SONGMANAGER","COULDN'T UPDATE MEDIA FILE FOR NEW FILES");
			Log.e("SONGMANAGER", e.getMessage());
		}
    }
    public void updateOldMusic() {
    	File tmp = new File(WRITE_PATH, "tmp.txt");
    	try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(writeFile));
	    	BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmp, true));
			
	    	bufferedWriter.write(mediaFiles.size() + "\n");
	    	
	        int size = Integer.parseInt(bufferedReader.readLine());
	        //Log.w("SONGMANAGER","SIZE: " + size);
	        
			for(int i = 0; i < size; i++) {
				//Log.w("SONGMANAGER","i: " + i);
				String title = bufferedReader.readLine();
				String artist = bufferedReader.readLine();
				String path = bufferedReader.readLine();
				//Log.w("SONGMANAGER","TITLE: " + title);
				//Log.w("SONGMANAGER","ARTIST: " + artist);
				//Log.w("SONGMANAGER","PATH: " + path);
				File f = new File(path);
				if(mediaFiles.contains(f))
				{
					bufferedWriter.write(title+"\n");
					bufferedWriter.write(artist+"\n");
					bufferedWriter.write(path+"\n");
					mediaFiles.remove(f);
				}	
			}
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			Log.e("SONGMANAGER","COULDN'T UPDATE MEDIA FILE FOR OLD FILES");
			Log.e("SONGMANAGER", e.getMessage());
		}
    }
    
    public void updateNewMusic() {
    	File tmp = new File(WRITE_PATH, "tmp.txt");
	    	try {
	    		BufferedReader bufferedReader = new BufferedReader(new FileReader(writeFile));
	    		bufferedReader.readLine();
	    		bufferedReader.close();
	    		
		    	BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmp, true));
		    	
		    	AudioFile audioFile;
		    	Tag tag;
		    	String path;
		    	
		    	for(File f : mediaFiles) {
		    		path = f.getAbsolutePath();
		    		String ext = FilenameUtils.getExtension(path);
		    		//Log.w("SONGMANAGER","PATH: " + path + " WITH EXT: " + ext);
	 					try {
	 						audioFile = AudioFileIO.read(f);
	 						if(ext.equals("mp3") || ext.equals("MP3")) {
	 							//Log.w("SONGMANAGER","READING MP3");
					    		tag = audioFile.getTag();
					    		String title = tag.getFirst(FieldKey.TITLE);
					    		String artist = tag.getFirst(FieldKey.ARTIST);
					    		if(title.equals("")) {
					    			title = f.getName();
					    		}
					    		if(artist.equals("")) {
					    			artist = "unknown artist";
					    		}
					    		
	 		            		bufferedWriter.write(title + "\n");
	 		            		bufferedWriter.write(artist + "\n");
	 		            		bufferedWriter.write(path + "\n");
	 						}
	 						else if(ext.equals("m4a") || ext.equals("M4A")) {
	 							//Log.w("SONGMANAGER","READING M4A");
	 							Mp4Tag mp4tag = (Mp4Tag)audioFile.getTag();
	 							bufferedWriter.write(mp4tag.getFirst(Mp4FieldKey.TITLE) + "\n");
	 		            		bufferedWriter.write(mp4tag.getFirst(Mp4FieldKey.ARTIST) + "\n");
	 							bufferedWriter.write(path + "\n");
	 						}
	 					} catch (Exception e) {
	 						bufferedWriter.write(f.getName() + "\n");
	 						bufferedWriter.write("unknown artist\n");
	 						bufferedWriter.write(f.getAbsolutePath() + "\n");
	 						Log.e("SONGMANAGER","COULDN'T READ MEDIA FILE AT: " + f.getPath());
	 						//Log.w("SONGMANAGER", e.getMessage());
	 						continue;
	 					}
		    	}
				bufferedWriter.close();
			} catch (IOException e) {
				Log.e("SONGMANAGER","COULDN'T WRITE NEW MEDIA FILE");
				Log.e("SONGMANAGER", e.getMessage());
			}
    }
    
    public void loadMusicList() {
    	  try{
	    	  BufferedReader bufferedReader = new BufferedReader(new FileReader(writeFile));
              int size = Integer.parseInt(bufferedReader.readLine());
           
	              for(int i = 0; i < size; i++){
	            	 Map<String,String> song = new HashMap<String,String>();
	            	 song.put("title", bufferedReader.readLine());
	            	 song.put("artist", bufferedReader.readLine());
	            	 song.put("path", bufferedReader.readLine());
	            	 song.put("ownerId",id);
	            	 
	            	 //Log.w("SONGMANAGER", "LOADING TITLE: " + song.get("title"));
	            	 //Log.w("SONGMANAGER", "LOADING ARTIST: " + song.get("artist"));
	            	 //Log.w("SONGMANAGER", "LOADING PATH: " + song.get("path"));
	            	 //Log.w("SONGMANAGER", "LOADING OWNER ID: " + song.get("ownerId"));
	                 songsList.add(song);
	              }
	          bufferedReader.close();
           } catch (FileNotFoundException e) {
        	   Log.e("SONGMANAGER","FILE NOT FOUND");
        	   Log.e("SONGMANAGER", e.getMessage());
           } catch (IOException e){
        	   Log.e("SONGMANAGER","IO EXCEPTION");
        	   Log.e("SONGMANAGER", e.getMessage());
           }
    	   //Log.w("SONGMANAGER","ALL SONGS LOADED");
    }
}