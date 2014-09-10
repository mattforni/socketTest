package com.example.sockettest.server.ui;

import static com.example.sockettest.utils.Logger.tag;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.sockettest.R;
import com.example.sockettest.server.Server;

public class PlayerControls {
    private final Server server;
    private final ImageButton nextButton, playButton, previousButton;

    public PlayerControls(final Server server) {
        this.server = server;

        this.nextButton = (ImageButton) server.findViewById(R.id.next_button);
        this.playButton = (ImageButton) server.findViewById(R.id.play_button);
        this.previousButton = (ImageButton) server.findViewById(R.id.previous_button);

        nextButton.setOnClickListener(new NextListener());
        playButton.setOnClickListener(new PlayListener());
        previousButton.setOnClickListener(new PreviousListener());
    }

    private class NextListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Previous clicked");
            server.next();
        }
    }

    private class PlayListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Play/Pause clicked");
            Drawable drawable = null;
            if (server.isPlaying()) {
                drawable = server.getResources().getDrawable(R.drawable.play_button);
            } else {
                drawable = server.getResources().getDrawable(R.drawable.pause_button);
            }
            playButton.setImageDrawable(drawable);
        }
    }

    private class PreviousListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Previous clicked");
            server.previous();
        }
    }
}
