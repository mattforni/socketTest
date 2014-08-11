package com.example.sockettest;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Client extends Activity {
	private String address;
	private int port;
	private TextView id;
	private String musicList;
	private String clientId;
	Utilities utilities  = new Utilities();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_view);
		
		id = (TextView) findViewById(R.id.client_id);
		
		utilities = new Utilities();
		clientId = utilities.getUUID();
		
		Intent intent = getIntent();
		
		address = intent.getStringExtra("ADDRESS");
		port = intent.getIntExtra("PORT", 0);
				
		Log.w("CLIENT","ADDRESS: " + address);
		Log.w("CLIENT","PORT: " + port);
		
		id.setText(id.getText() + "/" + address + ":" + port);
		
		ClientThread clientThread = new ClientThread(address, port);
		clientThread.start();
	}
}