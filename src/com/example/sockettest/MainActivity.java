package com.example.sockettest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.sockettest.server.Server;

public class MainActivity extends ActionBarActivity {

	private Button hostButton;
	private Button clientButton;
	private Button serverButton;
	private TextView addressTextView;
	private TextView portTextView;
	private Utilities utilities;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.host_client_view);
		
		hostButton = (Button)findViewById(R.id.host);
		serverButton = (Button)findViewById(R.id.server);
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
				intent.putExtra(Server.ADDRESS_KEY, getAddress());
				intent.putExtra(Server.PORT_KEY, getPort());
				startActivity(intent);
				finish();
			}
        });
		serverButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Server.class);
                intent.putExtra(Server.ADDRESS_KEY, getAddress());
                intent.putExtra(Server.PORT_KEY, getPort());
                startActivity(intent);
                finish();
            }
        });
		clientButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Client.class);
				intent.putExtra("ADDRESS", getAddress());
				intent.putExtra("PORT", getPort());
				startActivity(intent);
				finish();
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
