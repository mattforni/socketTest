package com.example.sockettest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

public class Client extends Activity implements OnTabChangeListener {
	private String address;
	private int port;
	private TextView id;
	private String clientId;
	private Utilities utilities  = new Utilities();
	private ListView libraryListView;
	private ListView playlistListView;
	private SongsManager songManager;
	private SimpleAdapter libraryListAdapter;
	private SimpleAdapter playlistListAdapter;
	private List<Map<String,String>> libraryViewList;
	private Queue<Map<String,String>> playlistViewList;
	private ClientThread clientThread;
	private Handler musicLibraryHandler;
	private Handler playlistHandler;
	private Handler recieveIdHandler;
	private Handler UIHandler;
	private Handler dequeuePlaylistHandler;
	private TabHost host;
	private TextView currentArtist;
	private TextView currentTitle;
	private Map<String,String> currentSong;
	
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_view);
		
		getActionBar().hide();
		
		id = (TextView) findViewById(R.id.client_id);
		
		utilities = new Utilities();
		
		Intent intent = getIntent();
		address = intent.getStringExtra("ADDRESS");
		port = intent.getIntExtra("PORT", 0);
		
		currentArtist = (TextView) findViewById(R.id.current_artist);
		currentTitle = (TextView) findViewById(R.id.current_title);
		
		currentTitle.setText("WAITING");
		currentArtist.setText("ON HOST");
		
		recieveIdHandler = new Handler() {
			@Override
 			public void handleMessage(Message msg) {
				clientId = clientThread.getClientId();
 				initializeUI();
			}
		};
		
		dequeuePlaylistHandler = new Handler() {
			@Override
 			public void handleMessage(Message msg) {
 				Log.w("CLIENT","DEQUEUE");
 				playlistViewList.remove();
 				playlistListAdapter.notifyDataSetChanged();
			}
		};
		
		playlistViewList = new LinkedList<Map<String,String>>();
		playlistListView = (ListView) findViewById(R.id.host_playlist_list_view);
		playlistListAdapter = new SimpleAdapter(this, (List <Map<String,String>>)playlistViewList, R.layout.list_item, new String[] {"title","artist"}, new int[] {R.id.list_item_title, R.id.list_item_artist});
		playlistListView.setAdapter(playlistListAdapter);
			
		UIHandler = new Handler() {
			@Override
 			public void handleMessage(Message msg) {
 				Log.w("CLIENT","UPDATING CURRENT SONG PLAYING");
 				currentSong = clientThread.getCurrentSong();
 				currentTitle.setText(currentSong.get("title"));
 				currentArtist.setText(currentSong.get("artist"));
 				currentTitle.setSelected(true);
 				currentArtist.setSelected(true);
			}
		};
		
		playlistHandler = new Handler() {
			@Override
 			public void handleMessage(Message msg) {
 				Log.w("CLIENT","UPDATING PLAYLIST");
 				List<Map<String,String>> songs = clientThread.getEnqueuedSongs();
 				for(Map<String,String> song: songs) {
	 				playlistViewList.add(song);
	 				playlistListAdapter.notifyDataSetChanged();
 				}
			}
		};
		
		musicLibraryHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				Log.w("CLIENT","UPDATING UITHREAD");
 				List<Map<String,String>> toBeAddedList = clientThread.getNewMusic();
 				Log.w("CLIENT","SIZE: " + toBeAddedList.size());
 				for(Map<String,String> song : toBeAddedList) {
 					Log.w("CLIENT","ID: " + clientId);
 					if (!song.get("ownerId").equals(clientId)) {
	 					libraryViewList.add(song);
	 					Collections.sort(libraryViewList,utilities.songComparator);
	 				    libraryListAdapter.notifyDataSetChanged();
 					}
 				}
 				super.handleMessage(msg);
 			}
 		};
		
		libraryListView = (ListView) findViewById(R.id.host_library_list_view);
		
		
		clientThread = new ClientThread(address, port, libraryViewList, musicLibraryHandler, recieveIdHandler, UIHandler, playlistHandler, dequeuePlaylistHandler);
		clientThread.start();
		
		Log.w("CLIENT","CLIENT THREAD STARTED");

		id.setText("CLIENT AT: " + address + ":" + port);
		
		initializeTabsSetup();
		
		libraryListView.setOnItemLongClickListener (new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
				Map<String, String> song = libraryViewList.get(position);
				
				clientThread.enqueue(song);
				return false;
			}
		});
	}
	
    private void initializeTabsSetup() {
    	host = (TabHost)findViewById(R.id.tabhost);  
    	//pager = (ViewPager) findViewById(R.id.pager);  
    	   
    	host.setup();  
    	TabSpec spec = host.newTabSpec("tab1");  
    	spec.setContent(R.id.library);  
    	spec.setIndicator("LIBRARY");   
    	host.addTab(spec);  
    	   
    	spec = host.newTabSpec("tab2");  
    	spec.setContent(R.id.playlist);  
    	spec.setIndicator("PLAYLIST");  
    	host.addTab(spec);
    	
    	spec = host.newTabSpec("tab3");  
    	spec.setContent(R.id.settings);  
    	spec.setIndicator("SETTINGS");  
    	host.addTab(spec);
    	
     	host.setOnTabChangedListener(this);
     }

	public void onTabChanged(String tabId){  
		int pageNumber = 0;  
		if(tabId.equals("tab1")){  
			pageNumber = 0;  
		} else if(tabId.equals("tab2")){  
			pageNumber = 1;  
		} else{  
			pageNumber = 2;  
		}
	}
	
	private void initializeUI() {
		songManager = new SongsManager(clientId);
		libraryViewList = songManager.getListViewList();
		Collections.sort(libraryViewList,utilities.songComparator);
		libraryListAdapter = new SimpleAdapter(this, libraryViewList, R.layout.list_item, new String[] {"title","artist"}, new int[] {R.id.list_item_title, R.id.list_item_artist});
		libraryListView.setAdapter(libraryListAdapter);
		
 	    libraryListView.setOnItemLongClickListener(new OnItemLongClickListener() {
	 	    public boolean onItemLongClick(AdapterView adapterView, View view, int position, long id) {
		 		Log.w("CLIENT","ENQUEUE ITEM: " + position);
		 		clientThread.enqueue(libraryViewList.get(position));
		 		return true;
	 		}
 		});
 	    clientThread.setMusicLibrary(libraryViewList);
	}
	
	@Override
	public void onDestroy() {
		clientThread.disconnect();
		Log.w("CLIENT","ONDESTROY CALLED");
	    super.onDestroy();
	}
}