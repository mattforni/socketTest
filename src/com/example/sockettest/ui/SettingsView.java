package com.example.sockettest.ui;

import android.widget.TextView;

import com.example.sockettest.Device;
import com.example.sockettest.R;

@SuppressWarnings("unused")
public class SettingsView {
    private final String[] ADAPTER_FROM = new String[] {"title","artist"};
    private final int ADAPTER_RESOURCE = R.layout.list_item;
    private final int[] ADAPTER_TO = new int[] {R.id.list_item_title, R.id.list_item_artist};
    private final int SERVER_LOCATION = R.id.server_location;

    private final Device device;
    private final String address;
    private final int port;
    private final TextView serverLocation;

    public SettingsView(final Device device) {
        this.device = device;

        port = device.getPort();
        address = device.getAddress();
        serverLocation = (TextView) device.findViewById(SERVER_LOCATION);

        if (device.isServer()) {
            serverLocation.setText("HOST AT: " + address + " / " + port);
        } else {
            serverLocation.setText("CLIENT AT: " + address + " / " + port);
        }
    }
}