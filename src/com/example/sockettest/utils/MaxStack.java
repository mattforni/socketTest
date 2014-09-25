package com.example.sockettest.utils;

import static com.example.sockettest.utils.Logger.tag;
import static java.lang.String.format;
import java.util.Stack;
import android.util.Log;
import com.example.sockettest.music.Song.MP3Song;

public class MaxStack<T> extends Stack<T> {
	private static final long serialVersionUID = 1870866892585800360L;
	private static final int STACK_BOTTOM = 0;
	private final int maxSize;
	
	public MaxStack(int maxSize) {
		super();
		this.maxSize = maxSize;
		super.setSize(this.maxSize);
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public T push(T object) {
		if(this.size() >= getMaxSize()) {
			dropBottom();
		}
		super.push(object);
		return object;
	}
	
	private void dropBottom() {
		Log.i(tag(this), format("Dropping song '%s'", this.get(STACK_BOTTOM)));
		super.removeElementAt(STACK_BOTTOM);
	}
}
