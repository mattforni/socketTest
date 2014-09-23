package com.example.sockettest.ui;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sockettest.Device;
import com.example.sockettest.R;

public class SearchBar {
    private final Device device;
    private final ImageButton button;
    private final LibraryView libraryView;
    private final OnClickListener clearSearchListener, startSearchListener;
    private final TextView field;

    public SearchBar(final Device device, final LibraryView libraryView) {
        this.device = device;
        this.libraryView = libraryView;

        this.clearSearchListener = new ClearSearchListener();
        this.startSearchListener = new StartSearchListener();

        this.button = (ImageButton) device.findViewById(R.id.search_button);
        this.field = (TextView) device.findViewById(R.id.search_box);
        
        button.setOnClickListener(startSearchListener);
    }

    private class ClearSearchListener implements OnClickListener {
        private static final int SEARCH_DRAWABLE = R.drawable.abc_ic_search_api_holo_light;

        @Override
        public void onClick(final View v) {
            Log.w(tag(this), "Clearing search");
            libraryView.hideSearch();
            field.setText("");
            field.setEnabled(true);
            button.setImageDrawable(device.getResources().getDrawable(SEARCH_DRAWABLE));
            button.setOnClickListener(startSearchListener);
        }
    }

    private class StartSearchListener implements OnClickListener {
        private static final int CLEAR_DRAWABLE = R.drawable.abc_ic_clear_holo_light;

        @Override
        public void onClick(final View v) {
            final String query = field.getText().toString();
            Log.w(tag(this), format("Searching for: %s", query));
            libraryView.showSearch(query);
            field.setEnabled(false);
            field.setText("searched: "+ query);
            button.setImageDrawable(device.getResources().getDrawable(CLEAR_DRAWABLE));
            button.setOnClickListener(clearSearchListener);
        }
    }
}
