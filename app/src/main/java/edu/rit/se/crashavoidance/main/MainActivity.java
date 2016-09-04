package edu.rit.se.crashavoidance.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.rit.se.crashavoidance.R;
import edu.rit.se.crashavoidance.chat.ChatFragment;
import edu.rit.se.crashavoidance.game.tictactoe.TicTacToeFragment;
import edu.rit.se.crashavoidance.infrastructure.WiFiDirectHandlerAccessor;
import edu.rit.se.crashavoidance.infrastructure.activity.BaseActivity;
import edu.rit.se.crashavoidance.log.LogsDialogFragment;
import edu.rit.se.wifibuddy.DnsSdService;
import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * The main Activity of the application, which is a container for Fragments and the ActionBar.
 * Contains WifiDirectHandler, which is a service
 * MainActivity has a Communication BroadcastReceiver to handle Intents fired from WifiDirectHandler.
 */
public class MainActivity extends BaseActivity implements WiFiDirectHandlerAccessor,CommunicationView {

    private WifiDirectHandler wifiDirectHandler;
    private boolean wifiDirectHandlerBound = false;
    private ChatFragment chatFragment = null;
    private LogsDialogFragment logsDialogFragment;
    private MainFragment mainFragment;
    private TicTacToeFragment ticTacToeFragment;

    @BindView(R.id.thisDeviceInfoTextView)
    TextView deviceInfoTextView;
    @BindView(R.id.mainToolbar)
    Toolbar toolbar;

    @Inject
    MainActivityPresenter mainActivityPresenter;

    private static final String TAG = WifiDirectHandler.TAG + "MainActivity";
    private CommunicationReceiver communicationReceiver;

    /**
     * Sets the UI layout for the Activity.
     * Registers a Communication BroadcastReceiver so the Activity can be notified of
     * intents fired in WifiDirectHandler, like Service Connected and Messaged Received.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating MainActivity");
        setContentView(R.layout.activity_main);

        initializeInjector();
        activityComponent.inject(this);

        ButterKnife.bind(this);

        // Initialize ActionBar
        setSupportActionBar(toolbar);

        registerCommunicationReceiver();
        Log.i(TAG, "MainActivity created");

        Intent intent = new Intent(this, WifiDirectHandler.class);
        bindService(intent, wifiServiceConnection, BIND_AUTO_CREATE);

        mainActivityPresenter.setView(this);
    }

    /**
     * Set the CommunicationReceiver for receiving intents fired from the WifiDirectHandler
     * Used to update the UI and receive communication messages
     */
    private void registerCommunicationReceiver() {
        communicationReceiver = new CommunicationReceiver(this);
        IntentFilter filter = getIntentFilterWifiDirectHandler();
        LocalBroadcastManager.getInstance(this).registerReceiver(communicationReceiver, filter);
        Log.i(TAG, "Communication Receiver registered");
    }

    @NonNull
    private IntentFilter getIntentFilterWifiDirectHandler() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiDirectHandler.Action.SERVICE_CONNECTED);
        filter.addAction(WifiDirectHandler.Action.MESSAGE_RECEIVED);
        filter.addAction(WifiDirectHandler.Action.DEVICE_CHANGED);
        filter.addAction(WifiDirectHandler.Action.WIFI_STATE_CHANGED);
        return filter;
    }

    /**
     * Adds the Main Menu to the ActionBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Called when a MenuItem in the Main Menu is selected
     * @param item Item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_logs:
                // View Logs MenuItem tapped
                viewLogsDialog();
                return true;
            case R.id.action_exit:
                // Exit MenuItem tapped
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewLogsDialog() {
        if (logsDialogFragment == null) {
            logsDialogFragment = new LogsDialogFragment();
        }
        logsDialogFragment.show(getFragmentManager(), "dialog");
    }

    // TODO: BRETT, add JavaDoc
    // Note: This is used to run WifiDirectHandler as a Service instead of being coupled to an
    //          Activity. This is NOT a connection to a P2P service being broadcast from a device
    private ServiceConnection wifiServiceConnection = new ServiceConnection() {

        /**
         * Called when a connection to the Service has been established, with the IBinder of the
         * communication channel to the Service.
         * @param name The component name of the service that has been connected
         * @param service The IBinder of the Service's communication channel
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Binding WifiDirectHandler service");
            Log.i(TAG, "ComponentName: " + name);
            Log.i(TAG, "Service: " + service);
            WifiDirectHandler.WifiTesterBinder binder = (WifiDirectHandler.WifiTesterBinder) service;

            wifiDirectHandler = binder.getService();
            wifiDirectHandlerBound = true;
            Log.i(TAG, "WifiDirectHandler service bound");

            // Add MainFragment to the 'fragment_container' when wifiDirectHandler is bound
            mainFragment = new MainFragment();
            replaceFragment(mainFragment);

            deviceInfoTextView.setText(wifiDirectHandler.getThisDeviceInfo());
        }

        /**
         * Called when a connection to the Service has been lost.  This typically
         * happens when the process hosting the service has crashed or been killed.
         * This does not remove the ServiceConnection itself -- this
         * binding to the service will remain active, and you will receive a call
         * to onServiceConnected when the Service is next running.
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            wifiDirectHandlerBound = false;
            Log.i(TAG, "WifiDirectHandler service unbound");
        }
    };

    /**
     * Returns the wifiDirectHandler
     * @return The wifiDirectHandler
     */
    @Override
    public WifiDirectHandler getWifiHandler() {
        return wifiDirectHandler;
    }

    /**
     * Initiates a P2P connection to a service when a Service ListItem is tapped.
     * An invitation appears on the other device to accept or decline the connection.
     * @param service The service to connect to
     */
    public void onServiceClick(DnsSdService service) {
        Log.i(TAG, "\nService List item tapped");

        if (service.getSrcDevice().status == WifiP2pDevice.CONNECTED) {
//            showChatFragment();
            showTicTacToeFragment();
        } else if (service.getSrcDevice().status == WifiP2pDevice.AVAILABLE) {
            String sourceDeviceName = service.getSrcDevice().deviceName;
            if (sourceDeviceName.equals("")) {
                sourceDeviceName = "other device";
            }
            Toast.makeText(this, "Inviting " + sourceDeviceName + " to connect", Toast.LENGTH_LONG).show();
            wifiDirectHandler.initiateConnectToService(service);
        } else {
            Log.e(TAG, "Service not available");
            Toast.makeText(this, "Service not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying MainActivity");
        if (wifiDirectHandlerBound) {
            Log.i(TAG, "WifiDirectHandler service unbound");
            unbindService(wifiServiceConnection);
            wifiDirectHandlerBound = false;
            LocalBroadcastManager.getInstance(this).unregisterReceiver(communicationReceiver);
            Log.i(TAG, "MainActivity destroyed");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Image captured");
//        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            chatFragment.pushImage(imageBitmap);
//        }
    }

    @Override
    public void showChatFragment() {
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
        }
        replaceFragment(chatFragment);
        Log.i(TAG, "Switching to Chat fragment");
    }

    @Override
    public void showTicTacToeFragment() {
        if (ticTacToeFragment == null) {
            ticTacToeFragment = new TicTacToeFragment();
        }
        replaceFragment(ticTacToeFragment);
        Log.i(TAG, "Switching to TicTacToe fragment");
    }

    @Override
    public void onDeviceChange() {
        if(wifiDirectHandler!=null) {
            deviceInfoTextView.setText(wifiDirectHandler.getThisDeviceInfo());
        }
    }

    @Override
    public void onMessageReceived(byte[] byteArrayMessage) {
//        if(chatFragment != null) {
//            chatFragment.pushMessage(byteArrayMessage);
//        }
        if(ticTacToeFragment != null){
            ticTacToeFragment.pushMessage(byteArrayMessage);
        }
    }

    @Override
    public void onWifiStateChanged() {
        mainFragment.handleWifiStateChanged();
    }

//    protected void onPause() {
//        super.onPause();
//        Log.i(TAG, "Pausing MainActivity");
//        if (wifiDirectHandlerBound) {
//            Log.i(TAG, "WifiDirectHandler service unbound");
//            unbindService(wifiServiceConnection);
//            wifiDirectHandlerBound = false;
//        }
//        Log.i(TAG, "MainActivity paused");
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i(TAG, "Resuming MainActivity");
//        Intent intent = new Intent(this, WifiDirectHandler.class);
//        if(!wifiDirectHandlerBound) {
//            bindService(intent, wifiServiceConnection, BIND_AUTO_CREATE);
//        }
//        Log.i(TAG, "MainActivity resumed");
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i(TAG, "Starting MainActivity");
//        Intent intent = new Intent(this, WifiDirectHandler.class);
//        bindService(intent, wifiServiceConnection, BIND_AUTO_CREATE);
//        Log.i(TAG, "MainActivity started");
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i(TAG, "Stopping MainActivity");
//        if(wifiDirectHandlerBound) {
//            Intent intent = new Intent(this, WifiDirectHandler.class);
//            stopService(intent);
//            unbindService(wifiServiceConnection);
//            wifiDirectHandlerBound = false;
//        }
//        Log.i(TAG, "MainActivity stopped");
//    }
}
