package edu.rit.se.crashavoidance.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * BroadcastReceiver used to receive Intents fired from the WifiDirectHandler when P2P events occur
 * Used to update the UI and receive communication messages
 */
public class CommunicationReceiver extends BroadcastReceiver {

    private static final String TAG = WifiDirectHandler.TAG + "CommReceiver";

    private final CommunicationView communicationView;

    public CommunicationReceiver(CommunicationView communicationView) {
        this.communicationView = communicationView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the intent sent by WifiDirectHandler when a service is found
        if (intent.getAction().equals(WifiDirectHandler.Action.SERVICE_CONNECTED)) {
            // This device has connected to another device broadcasting the same service
            Log.i(TAG, "Service connected");
//            communicationView.showChatFragment();
            communicationView.showTicTacToeFragment();
        } else if (intent.getAction().equals(WifiDirectHandler.Action.DEVICE_CHANGED)) {
            // This device's information has changed
            Log.i(TAG, "This device changed");
            communicationView.onDeviceChange();
        } else if (intent.getAction().equals(WifiDirectHandler.Action.MESSAGE_RECEIVED)) {
            // A message from the Communication Manager has been received
            Log.i(TAG, "Message received");
            communicationView.onMessageReceived(intent.getByteArrayExtra(WifiDirectHandler.MESSAGE_KEY));
        } else if (intent.getAction().equals(WifiDirectHandler.Action.WIFI_STATE_CHANGED)) {
            // Wi-Fi has been enabled or disabled
            Log.i(TAG, "Wi-Fi state changed");
            communicationView.onWifiStateChanged();
        }
    }
}