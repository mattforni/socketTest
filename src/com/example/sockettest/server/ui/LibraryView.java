package com.example.sockettest.server.ui;

import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sockettest.R;
import com.example.sockettest.server.Server;

public class LibraryView {
    private final Server server;
    private final SearchBar searchBar;
    private final ListView libraryView, searchView;
    private final PlayerControls controls;

    public LibraryView(final Server server) {
        this.server = server;
        this.searchBar = new SearchBar(server);
        this.libraryView = (ListView)server.findViewById(R.id.host_library_list_view);
        this.libraryView.setAdapter(new SimpleAdapter(server, server.getLibrary(),
                R.layout.list_item, new String[] {"title","artist"},
                new int[] {R.id.list_item_title, R.id.list_item_artist}));
        this.controls = new PlayerControls(server);
        this.searchView = (ListView)server.findViewById(R.id.host_search_list_view);
        this.searchView.setAdapter(new SimpleAdapter(this, searchViewList, R.layout.list_item, new String[] {"title","artist"}, new int[] {R.id.list_item_title, R.id.list_item_artist}););
        this.searchView.setVisibility(View.GONE);
    }

    public void startSearch(final String query) {
        libraryView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        server.search(query);
    }

    public final void clearSearch() {
        while(searchViewList.size() != 0) {
            searchViewList.remove(0);
            searchListAdapter.notifyDataSetChanged();
        }
        searchView.setVisibility(View.GONE);
        libraryView.setVisibility(View.VISIBLE);
    }
}
