package edu.rit.se.crashavoidance.views;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.rit.se.crashavoidance.R;
import edu.rit.se.crashavoidance.wifi.DnsSdService;
import edu.rit.se.crashavoidance.wifi.WifiDirectHandler;

/**
 * ListFragment that shows a list of available discovered services
 */
public class AvailableServicesFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private WifiDirectHandler wifiDirectHandler;
    List<DnsSdService> services = new ArrayList<>();
    AvailableServicesListViewAdapter servicesListAdapter;
    MainActivity mainActivity;
    WifiDirectReceiver receiver;

    /**
     * Sets the Layout for the UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_services, container, false);
    }

    /**
     * Sets the WifiDirectHandler instance when AvailableServicesFragment is attached to MainActivity
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * Sets the service list adapter to display available services
     */
    private void setServiceList() {
        servicesListAdapter = new AvailableServicesListViewAdapter((MainActivity) getActivity(), services);
        setListAdapter(servicesListAdapter);
    }

    /**
     * Onclick Method for the the reset button to clear the services list
     * and start discovering services again
     */
    public void resetServiceDiscovery(){
        // Clear the list, notify the list adapter, and start discovering
        // services again
        services.clear();
        servicesListAdapter.notifyDataSetChanged();
        wifiDirectHandler.startDiscoveringServices();
    }


    /**
     * Registers the receiver to listen for the intents broadcast by WifiDirectHandler
     * and calls service discovery
     */
    private void startDiscoveringServices() {
        receiver = new WifiDirectReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
        wifiDirectHandler.startDiscoveringServices();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Receiver for receiving intents from the WifiDirectHandler to update UI
     * when Wi-Fi Direct commands are completed
     */
    public class WifiDirectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the intent sent by WifiDirectHandler when a service is found
            if (intent.getAction().equals(WifiDirectHandler.Action.DNS_SD_SERVICE_AVAILABLE)) {
                String serviceKey = intent.getStringExtra(wifiDirectHandler.SERVICE_MAP_KEY);
                DnsSdService service = wifiDirectHandler.getDnsSdServiceMap().get(serviceKey);
                // Add the service to the UI and update
                servicesListAdapter.addUnique(service);
                Log.i(wifiDirectHandler.LOG_TAG, "Found service for device " + service.getSrcDevice().deviceName);
                // TODO Capture an intent that indicates the peer list has changed
                // and see if we need to remove anything from our list
            }
        }
    }

    /**
     * This is called when the Fragment is opened and is attached to MainActivity
     * Sets the ListAdapter for the Service List and initiates the service discovery
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            WiFiDirectHandlerAccessor wifiDirectHandlerAccessor = ((WiFiDirectHandlerAccessor) getActivity());
            wifiDirectHandler = wifiDirectHandlerAccessor.getWifiHandler();
            setServiceList();
            startDiscoveringServices();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement WiFiDirectHandlerAccessor");
        }
    }
}
