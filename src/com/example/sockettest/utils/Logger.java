package com.example.sockettest.utils;

public abstract class Logger {
	public static final String tag(final Object object) {
		return object.getClass().getSimpleName();
	}
}
