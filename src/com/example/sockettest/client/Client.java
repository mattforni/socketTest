package com.example.sockettest.client;

import java.util.Random;

import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.sockettest.Device;
import com.example.sockettest.R;
import com.example.sockettest.ui.LibraryView;

public class Client extends Device {
    public static final String ADDRESS_KEY = "ADDRESS";
    public static final String PORT_KEY = "PORT";

    private static final String ID = "SERVER";
    private static final Random RANDOM = new Random();

    public Client() {
        
    	// TODO Recieve ID from host
    	
    	super(ID);
    }

    @Override
    public boolean enqueueSong(final int position, final boolean fromSearch) {
        boolean enqueued = false;
        
        // TODO Send song to Server across network
        
        return enqueued;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_view);
        initializeTabs();
        this.libraryView = new LibraryView(this);
        this.libraryView.updateLibrary(songManager.getAllSongs());
    }

    private void initializeTabs() {
        TabHost tabs = (TabHost)findViewById(R.id.tabhost);

        tabs.setup();
        TabSpec spec = tabs.newTabSpec("tab1");
        spec.setContent(R.id.library);
        spec.setIndicator("LIBRARY");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tab2");
        spec.setContent(R.id.playlist);
        spec.setIndicator("PLAYLIST");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tab3");
        spec.setContent(R.id.settings);
        spec.setIndicator("SETTINGS");
        tabs.addTab(spec);

        tabs.setOnTabChangedListener(this);
     }
    
    @Override
    public void onTabChanged(final String tabId) {
        int pageNumber = 0;
        if(tabId.equals("tab1")){  
            pageNumber = 0;  
        } else if(tabId.equals("tab2")){  
            pageNumber = 1;  
        } else{  
            pageNumber = 2;  
        }
    }

    // TODO need to support streaming
    public final boolean pause() {
    	
    	// TODO send message to Server to pause accross network
    	
        return true;
    }

    // TODO need to support streaming
    public final boolean play() {
        
    	// TODO send message to Server to play accross network
    	
        return true;
    }

    public final boolean play(final int index, final boolean fromSearch) {
        
    	// TODO send message to Server to play accross network
    	
        return true;
    }

    public final boolean previous() {
        
    	// TODO send message to Server to previous across network
    	
        return false;
    }

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}
}