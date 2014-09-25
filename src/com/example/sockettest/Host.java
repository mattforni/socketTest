package com.example.sockettest;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Host extends Activity implements OnTabChangeListener {
	private String address;
	private int port;
	private TextView id;
	private Utilities utilities;
	private ServerThreadHandler serverthreadHandler;
	private ListView libraryListView;
	private ListView playlistListView;
	private ListView searchListView;
	private SongsManager songManager;
	private SimpleAdapter libraryListAdapter;
	private SimpleAdapter playlistListAdapter;
	private SimpleAdapter searchListAdapter;
	private List<Map<String,String>> libraryViewList;
	private List<Map<String,String>> searchViewList;
	private Queue<Map<String,String>> playlistViewList;
	private Handler updateUIHandler;
	private Handler playlistHandler;
	private Handler onStreamCompletionHandler;
	private Handler disconnectHandler;
	private TabHost host;
	private TextView currentArtist;
	private TextView currentTitle;
	private Map<String,String> currentSong;
	private MediaPlayer mediaPlayer;
	private int position;
	private boolean shuffle;
	private boolean playing;
	private boolean streaming;
	private boolean searching;
	private Switch shuffleSwitch;
	private StreamSinkThread streamSinkThread;
	private ImageButton playPauseButton;
	private ImageButton nextButton;
	private ImageButton previousButton;
	private ImageButton searchButton;
	private TextView searchBox;
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.host_view);
		
		getActionBar().hide();
		
		Intent intent = getIntent();
		
		utilities = new Utilities();
		songManager = new SongsManager("HOST");
		
		position = 0;
		shuffle = false;
		streaming = false;
		searching = false;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		    public void onCompletion(MediaPlayer mp) {
		        playSong(getNextSong());
		    }
		});

		searchBox = (TextView) findViewById(R.id.search_box);
		searchButton = (ImageButton) findViewById(R.id.search_button);
		
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(searching) {
					Log.w("HOST","CLEARING SEARCH");
					clearSearch();
					searchBox.setText("");
					searchButton.setImageDrawable(getResources().getDrawable(R.drawable.abc_ic_search_api_holo_light));
					searching = false;
				} else {
					String key = searchBox.getText().toString();
					Log.w("HOST","SEARCHING FOR: " + key );
					startSearch(key);
					searchButton.setImageDrawable(getResources().getDrawable(R.drawable.abc_ic_clear_holo_light));
					searching = true;
				}
			}
		});
		
		initializeTabsSetup();
		/*
		shuffleSwitch = (Switch) findViewById(R.id.shuffle_switch);
		shuffleSwitch.setChecked(false);
		shuffleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Log.w("HOST","SHUFFLE TURNED ON");
					shuffle = true;
			    } else {
			    	Log.w("HOST","SHUFFLE TURNED OFF");
			    	shuffle = false;
			    }
			}
		});
		*/
		
		libraryViewList = songManager.getListViewList();
		Log.w("HOST","LOADED ALL MUSIC");
		playlistViewList = new LinkedList<Map<String,String>>();
		searchViewList = new LinkedList<Map<String,String>>();
		
		libraryListView = (ListView) findViewById(R.id.host_library_list_view);
		playlistListView = (ListView) findViewById(R.id.host_playlist_list_view);
		searchListView = (ListView) findViewById(R.id.host_search_list_view);
		searchListView.setVisibility(View.GONE);
		
		Collections.sort(libraryViewList,utilities.songComparator);
		libraryListAdapter = new SimpleAdapter(this, libraryViewList, R.layout.list_item, new String[] {"title","artist"}, new int[] {R.id.list_item_title, R.id.list_item_artist});
		playlistListAdapter = new SimpleAdapter(this, (List)playlistViewList, R.layout.list_item, new String[] {"title","artist"}, new int[] {R.id.list_item_title, R.id.list_item_artist});
		searchListAdapter = new SimpleAdapter(this, searchViewList, R.layout.list_item, new String[] {"title","artist"}, new int[] {R.id.list_item_title, R.id.list_item_artist});
		
		libraryListView.setAdapter(libraryListAdapter);
		playlistListView.setAdapter(playlistListAdapter);
		searchListView.setAdapter(searchListAdapter);
		
		currentArtist = (TextView) findViewById(R.id.current_artist);
		currentTitle = (TextView) findViewById(R.id.current_title);
		
		address = intent.getStringExtra("ADDRESS");
		port = intent.getIntExtra("PORT", 0);
		
		id = (TextView) findViewById(R.id.client_id);
        id.setText("HOST AT: " + address + ":" + port);
        
        /*
        int i = 0;
        
        Map<Integer, Map<String, String>> labels = utilities.addLabels(libraryViewList);
        for(Integer x :labels.keySet()) {
        	libraryViewList.add(x, labels.get(x));
        	libraryListAdapter.notifyDataSetChanged();
        	i++;
        }
        */
        
        updateUIHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				Log.w("HOST","UPDATING UITHREAD");
 				String lastAddedId = serverthreadHandler.getLastAdded();
 				List<Map<String,String>> toBeAddedList = serverthreadHandler.getNewMusic(lastAddedId);
 				for(Map<String,String> song : toBeAddedList) {
 					libraryViewList.add(song);
 					Collections.sort(libraryViewList,utilities.songComparator);
 				    libraryListAdapter.notifyDataSetChanged();
 				}
 				super.handleMessage(msg);
 				serverthreadHandler.updateMusicLibraryAllThreads();
 				serverthreadHandler.getServerThread(lastAddedId).initializePlaylist();
 				
 				if(currentSong != null) {
 					serverthreadHandler.getServerThread(lastAddedId).updateCurrentSong(currentSong);
 				}
 			}
 		};
 		
 		onStreamCompletionHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				Log.w("HOST","STREAM COMPLETE");
 				streamSinkThread.flush();
 				playSong(getNextSong());
 				streaming = false;
 				super.handleMessage(msg);
 			}
 		};
 		
 		playlistHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				Log.w("HOST","UPDATING PLAYLIST");
 				for(Map<String,String> song : serverthreadHandler.getEnqueuedSongsAllThreads()) {
 					playlistViewList.add(song);
 					playlistListAdapter.notifyDataSetChanged();
 					serverthreadHandler.updateplaylistAllThreads(song);
 				}
 				super.handleMessage(msg);
 			}
 		};
 		disconnectHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				Log.w("HOST","DISCONNECTING");
 				String disconId = serverthreadHandler.getDisconnectingThreadId();
 				
 				serverthreadHandler.removeThread(disconId);
 				
 				for(int i = 0; i < libraryViewList.size(); i++) {
 					Log.w("HOST","TITLE: " + libraryViewList.get(i).get("title"));
 					Log.w("HOST","ARTIST: " + libraryViewList.get(i).get("artist"));
 					Log.w("HOST","ID: " + libraryViewList.get(i).get("ownerId"));
 					if(libraryViewList.get(i).get("ownerId").equals(disconId)) {
 						Log.w("HOST","REMOVING FROM LIBRARY: " + libraryViewList.get(i).get("title"));
 						libraryViewList.remove(i);
 						libraryListAdapter.notifyDataSetChanged();
 						i--;
 					}
 				}
 				
 				for(int i = 0; i < playlistViewList.size(); i++) {
 					Log.w("HOST","TITLE: " + ((LinkedList<Map<String,String>>)playlistViewList).get(i).get("title"));
 					Log.w("HOST","ARTIST: " + ((LinkedList<Map<String,String>>)playlistViewList).get(i).get("artist"));
 					Log.w("HOST","ID: " + ((LinkedList<Map<String,String>>)playlistViewList).get(i).get("ownerId"));
 					if(((LinkedList<Map<String,String>>)playlistViewList).get(i).get("ownerId").equals(disconId)) {
 						Log.w("HOST","REMOVING FROM PLAYLIST: " + ((LinkedList<Map<String,String>>)playlistViewList).get(i).get("title"));
 						((LinkedList<Map<String,String>>)playlistViewList).remove(i);
 						playlistListAdapter.notifyDataSetChanged();
 						i--;
 					}
 				}
 				
 				super.handleMessage(msg);
 			}
 		};
        
        serverthreadHandler = new ServerThreadHandler(address, port, updateUIHandler, playlistHandler, disconnectHandler, libraryViewList, playlistViewList);
        serverthreadHandler.start();
        
        libraryListView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView adapterView, View view, int newPosition, long id) {
        		if(!streaming) {
        			
	 			   Log.w("HOST","PLAYING ITEM " + newPosition);
	 			   if(libraryViewList.get(newPosition).get("ownerId").equals("HOST")) {
	 				   streaming = true;
	 			   }
	 			   else {
	 				   streaming = false;
	 			   }
	 			   playing = true;
	 			   playSong(libraryViewList.get(newPosition));
	 			   position = newPosition;
        		}
 		    }
 		});
        
 	    libraryListView.setOnItemLongClickListener(new OnItemLongClickListener() {
 		   public boolean onItemLongClick(AdapterView adapterView, View view, int position, long id) {
 			   Log.w("HOST","ENQUEUE ITEM: " + position);
 			   playlistViewList.add(libraryViewList.get(position));
 			   serverthreadHandler.updateplaylistAllThreads(libraryViewList.get(position));
 			   playlistListAdapter.notifyDataSetChanged();
 			   return true;
 		   }
 		 });
 	    
 	    
 	   searchListView.setOnItemClickListener(new OnItemClickListener() {
       	public void onItemClick(AdapterView adapterView, View view, int newPosition, long id) {
       		if(!streaming) {
       			
	 			   Log.w("HOST","PLAYING ITEM " + newPosition);
	 			   if(searchViewList.get(newPosition).get("ownerId").equals("HOST")) {
	 				   streaming = true;
	 			   }
	 			   else {
	 				   streaming = false;
	 			   }
	 			   playing = true;
	 			   playSong(searchViewList.get(newPosition));
	 			   position = newPosition;
       		}
		    }
		});
       
	    searchListView.setOnItemLongClickListener(new OnItemLongClickListener() {
		   public boolean onItemLongClick(AdapterView adapterView, View view, int position, long id) {
			   Log.w("HOST","ENQUEUE ITEM: " + position);
			   playlistViewList.add(searchViewList.get(position));
			   serverthreadHandler.updateplaylistAllThreads(searchViewList.get(position));
			   playlistListAdapter.notifyDataSetChanged();
			   return true;
		   }
		 });
 	    
 	    playing = false;
 	    
 	    playPauseButton = (ImageButton) findViewById(R.id.play_button);
 	    nextButton = (ImageButton) findViewById(R.id.next_button);
 	    previousButton = (ImageButton) findViewById(R.id.previous_button);
 	    
 	    playPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w("HOST","PAUSE/PLAY PRESSED");
				if (playing) {
					Log.w("HOST","PLAYING");
					if (streaming) {
						Log.w("HOST","STREAMING");
						streamSinkThread.pause();
					} else {
						Log.w("HOST","NOT STREAMING");
						mediaPlayer.pause();
					}
					playing = false;
					playPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
				} else {
					Log.w("HOST","NOT PLAYING");
					if (streaming) {
						Log.w("HOST","STREAMING");
						streamSinkThread.play();
					} else {
						Log.w("HOST","NOT STREAMING");
						mediaPlayer.start();
					}
					playPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
					playing = true;
				}
			}
 	    	
 	    });
 	    
 		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(streaming) {
					streamSinkThread.pause();
					streamSinkThread.flush();
					streaming = false;
				}
				playSong(getNextSong());
			}	
		});
 		
 		// FIGURE OUT HOW TO QUEUE PLAYED SONGS
 		// TODO
 		
 		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w("HOST","PREVIOUS SONG");
			}	
		});	
	}
	
	public Map<String,String> getNextSong() {
		Map<String,String> song = null;
		if(playlistViewList.isEmpty()) {
			if(shuffle) {
				Random r = new Random();
				song = libraryViewList.get(r.nextInt(libraryViewList.size()));
			}
			else {
				position++;
				song = libraryViewList.get(position);
				if(position >= libraryViewList.size()) {
					position = 0;
				}
			}
		}
		else {
			song = playlistViewList.remove();
			playlistListAdapter.notifyDataSetChanged();
			serverthreadHandler.dequeueAllThreads();
		}
		Log.w("HOST","RETURNING: " + song.get("title"));
		return song;
	}
	
	public void updatePlayingUI(Map<String,String> song) {
		currentTitle.setText(song.get("title"));
		currentArtist.setText(song.get("artist"));
		currentSong = song;
		currentTitle.setSelected(true);
		currentArtist.setSelected(true);
	}
	
	public void playSong(Map<String,String> song) {
		playPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
		if(streamSinkThread != null) {
			streamSinkThread.interrupt();
			streamSinkThread = null;
		}
		updatePlayingUI(song);
		String ownerId = song.get("ownerId");
		if(ownerId.equals("HOST")) {
			streaming = false;
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(song.get("path"));
				mediaPlayer.prepare();
				Log.w("HOST","PLAYING LOCALLY AT: " + song.get("path"));
				mediaPlayer.start();
			} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
				Log.e("HOST","MEDIAPLAYER ERROR");
				Log.e("HOST",e.getMessage());
			}
		}
		else {
			streaming = true;
			mediaPlayer.reset();
			
			streamSinkThread = new StreamSinkThread(song, onStreamCompletionHandler, address, port);
			streamSinkThread.start();
			
			serverthreadHandler.getServerThread(ownerId).streamMusic(song);
			Log.w("HOST","STREAMING AT: " + song.get("title"));
		}
		serverthreadHandler.updateCurrentSongAllThreads(song);
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

	@Override
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
	
	public void startSearch(String key) {
		for(int i = 0; i < libraryViewList.size(); i++) {
			Map<String,String> song = libraryViewList.get(i);
			String lowerTitle = song.get("title").toLowerCase(Locale.ENGLISH);
			String lowerArtist = song.get("artist").toLowerCase(Locale.ENGLISH);
			String lowerKey = key.toLowerCase(Locale.ENGLISH);
			if(lowerTitle.contains(lowerKey) || lowerArtist.contains(lowerKey)) {
				searchViewList.add(song);
				searchListAdapter.notifyDataSetChanged();
				Log.w("HOST","FOUND MATCH: " + song.get("title"));
			}
		}
		libraryListView.setVisibility(View.GONE);
		searchListView.setVisibility(View.VISIBLE);
	}
	
	public void clearSearch() {
		while(searchViewList.size() != 0) {
			searchViewList.remove(0);
			searchListAdapter.notifyDataSetChanged();
		}
		searchListView.setVisibility(View.GONE);
		libraryListView.setVisibility(View.VISIBLE);
	}
}
