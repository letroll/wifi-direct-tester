package edu.rit.se.crashavoidance.infrastructure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import edu.rit.se.crashavoidance.availableService.AvailableServicesListViewAdapter;
import edu.rit.se.wifibuddy.DnsSdService;
import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * Receiver for receiving intents from the WifiDirectHandler to update UI
 * when Wi-Fi Direct commands are completed
 */
public class WifiDirectReceiver extends BroadcastReceiver {
    private WifiDirectHandler wifiDirectHandler;
    private AvailableServicesListViewAdapter servicesListAdapter;

    public WifiDirectReceiver(WifiDirectHandler wifiDirectHandler, AvailableServicesListViewAdapter servicesListAdapter) {
        this.wifiDirectHandler = wifiDirectHandler;
        this.servicesListAdapter = servicesListAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the intent sent by WifiDirectHandler when a service is found
        if (intent.getAction().equals(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE)) {
            String serviceKey = intent.getStringExtra(WifiDirectHandler.SERVICE_MAP_KEY);
            DnsSdService service = wifiDirectHandler.getDnsSdServiceMap().get(serviceKey);
            Log.d("TIMING", "Service Discovered and Accessed " + (new Date()).getTime());
            // Add the service to the UI and update
            servicesListAdapter.addUnique(service);
            // TODO Capture an intent that indicates the peer list has changed
            // and see if we need to remove anything from our list
        }
    }
}