package com.example.sockettest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Host extends Activity {
	private String address;
	private int port;
	private ServerSocket server;
	private TextView id;
	private TextView files;
	private Utilities utilities;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.host_view);
		
		Intent intent = getIntent();
		
		utilities = new Utilities();
		
		address = intent.getStringExtra("ADDRESS");
		port = intent.getIntExtra("PORT", 0);
		id = (TextView)findViewById(R.id.client_id);
		files = (TextView)findViewById(R.id.host_music_library);
		
		files.setText("HOST:\n" + utilities.getMusicFiles() + "\n");
		
        id.setText(id.getText() + "/" + address + ":" + port);
	}
	
}
