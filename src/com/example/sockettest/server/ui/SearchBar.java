package com.example.sockettest.server.ui;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;
import com.example.sockettest.R;
import com.example.sockettest.server.Server;

public class SearchBar {
    private final Server server;
    private final TextView field;
    private final ImageButton button;

    public SearchBar(final Server server) {
        this.server = server;
        // TODO change name to search_field
        this.field = (TextView) server.findViewById(R.id.search_box);
        this.button = (ImageButton) server.findViewById(R.id.search_button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(server.isSearching()) {
                    Log.w(tag(this),"Clearing search");
                    clearSearch();
                    field.setText("");
                    button.setImageDrawable(server.getResources().getDrawable(R.drawable.abc_ic_search_api_holo_light));
                    searching = false;
                } else {
                    String key = field.getText().toString();
                    Log.w(tag(this), format("Searching for: %s", key));
                    startSearch(key);
                    button.setImageDrawable(server.getResources().getDrawable(R.drawable.abc_ic_clear_holo_light));
                    searching = true;
                }
            }
        });
    }
}
