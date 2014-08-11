package com.example.sockettest;

import com.example.sockettest.Client;
import com.example.sockettest.Host;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private Button hostButton;
	private Button clientButton;
	private TextView addressTextView;
	private TextView portTextView;
	private Utilities utilities;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.host_client_view);
		
		hostButton = (Button)findViewById(R.id.host);
		clientButton = (Button)findViewById(R.id.client);
		addressTextView = (TextView)findViewById(R.id.address_input);
		portTextView = (TextView)findViewById(R.id.port_input);
		
		utilities = new Utilities();
		
		addressTextView.setText(utilities.getIpAddress());
		portTextView.setText("8080");
		
		hostButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Host.class);
				intent.putExtra("ADDRESS", getAddress());
				intent.putExtra("PORT", getPort());
				startActivity(intent);
			}
        });
		clientButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Client.class);
				intent.putExtra("ADDRESS", getAddress());
				intent.putExtra("PORT", getPort());
				startActivity(intent);
			}
        });
	}
	
	private String getAddress() {
		return addressTextView.getText().toString();
	}
	
	private int getPort() {
		return Integer.parseInt(portTextView.getText().toString());
	}
}
