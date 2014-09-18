package com.example.sockettest.ui;

import static com.example.sockettest.utils.Logger.tag;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.sockettest.Device;
import com.example.sockettest.R;

public class PlayerControls {
    private static final int PAUSE_DRAWABLE = R.drawable.pause_button;
    private static final int PLAY_DRAWABLE = R.drawable.play_button;

    private final Device device;
    private final ImageButton nextButton, playButton, previousButton;
    private final OnClickListener pauseListener, playListener;

    public PlayerControls(final Device device) {
        this.device = device;

        this.nextButton = (ImageButton) device.findViewById(R.id.next_button);
        this.playButton = (ImageButton) device.findViewById(R.id.play_button);
        this.previousButton = (ImageButton) device.findViewById(R.id.previous_button);

        this.pauseListener = new PauseListener();
        this.playListener = new PlayListener();

        nextButton.setOnClickListener(new NextListener());
        playButton.setOnClickListener(playListener);
        previousButton.setOnClickListener(new PreviousListener());
    }

    public final void showPauseButton() {
        playButton.setImageDrawable(device.getResources().getDrawable(PAUSE_DRAWABLE));
        playButton.setOnClickListener(pauseListener);
    }

    public final void showPlayButton() {
        playButton.setImageDrawable(device.getResources().getDrawable(PLAY_DRAWABLE));
        playButton.setOnClickListener(playListener);
    }

    private class NextListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Next clicked");
            device.next();
        }
    }

    private class PauseListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Pause clicked");
            device.pause();
        }
    }

    private class PlayListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Play clicked");
            device.play();
        }
    }

    private class PreviousListener implements OnClickListener {
        @Override
        public void onClick(final View v) {
            Log.d(tag(v), "Previous clicked");
            device.previous();
        }
    }
}
