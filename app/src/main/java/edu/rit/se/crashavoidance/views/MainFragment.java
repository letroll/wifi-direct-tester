package edu.rit.se.crashavoidance.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.rit.se.crashavoidance.R;
import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * The Main Fragment of the application, which contains the Switches and Buttons to perform P2P tasks
 */
public class MainFragment extends Fragment {

    private WiFiDirectHandlerAccessor wifiDirectHandlerAccessor;

    @BindView(R.id.toggleWifiSwitch)
    Switch toggleWifiSwitch;
    @BindView(R.id.serviceRegistrationSwitch)
    Switch serviceRegistrationSwitch;
    @BindView(R.id.noPromptServiceRegistrationSwitch)
    Switch noPromptServiceRegistrationSwitch;
    @BindView(R.id.discoverServicesButton)
    Button discoverServicesButton;

    private AvailableServicesFragment availableServicesFragment;
    private MainActivity mainActivity;
    private static final String TAG = WifiDirectHandler.TAG + "MainFragment";
    private WifiDirectHandler wifiDirectHandler;

    /**
     * Sets the layout for the UI, initializes the Buttons and Switches, and returns the View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Sets the Layout for the UI
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);

        updateToggles();

        // Set Toggle Listener for Wi-Fi Switch
        toggleWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * Enable or disable Wi-Fi when Switch is toggled
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "\nWi-Fi Switch Toggled");
                // Disable or Enable Wi-Fi, disable or enable all switches and buttons
                wifiDirectHandler.setWifiEnabled(isChecked);
            }
        });

        // Set Toggle Listener for Service Registration Switch
        serviceRegistrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * Add or Remove a Local Service when Switch is toggled
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "\nService Registration Switch Toggled");
                if (isChecked) {
                    // Add local service
                    if (wifiDirectHandler.getWifiP2pServiceInfo() == null) {
                        HashMap<String, String> record = new HashMap<>();
                        record.put("Name", wifiDirectHandler.getThisDevice().deviceName);
                        record.put("Address", wifiDirectHandler.getThisDevice().deviceAddress);
                        wifiDirectHandler.addLocalService("Wi-Fi Buddy", record);
                        noPromptServiceRegistrationSwitch.setEnabled(false);
                    } else {
                        Log.w(TAG, "Service already added");
                    }
                } else {
                    // Remove local service
                    wifiDirectHandler.removeService();
                    noPromptServiceRegistrationSwitch.setEnabled(true);
                }
            }
        });

//        // Set Toggle Listener for No-Prompt Service Registration Switch
//        noPromptServiceRegistrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            /**
//             * Add or Remove a No-Prompt Local Service when Switch is toggled
//             */
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.i(TAG, "\nNo-Prompt Service Registration Switch Toggled");
//                if (isChecked) {
//                    // Add no-prompt local service
//                    ServiceData serviceData = new ServiceData(
//                            "Wi-Fi Direct Handler",         // Name
//                            4545,                           // Port
//                            new HashMap<String, String>(),  // Record
//                            ServiceType.PRESENCE_TCP        // Type
//                    );
//                    wifiDirectHandler.startAddingNoPromptService(serviceData);
//                    serviceRegistrationSwitch.setEnabled(false);
//                } else {
//                    // Remove no-prompt local service
//                    wifiDirectHandler.removeService();
//                    serviceRegistrationSwitch.setEnabled(true);
//                }
//            }
//        });
        return view;
    }

    /**
     * Show AvailableServicesFragment when Discover Services Button is clicked
     */
    @OnClick(R.id.discoverServicesButton)
    void discoverServices() {
        Log.i(TAG, "\nDiscover Services Button Pressed");
        if (availableServicesFragment == null) {
            availableServicesFragment = new AvailableServicesFragment();
        }
        mainActivity.replaceFragment(availableServicesFragment);
    }

    /**
     * Sets the Main Activity instance
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * Sets the WifiDirectHandler instance when MainFragment is attached to MainActivity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            wifiDirectHandlerAccessor = ((WiFiDirectHandlerAccessor) getActivity());
            wifiDirectHandler = wifiDirectHandlerAccessor.getWifiHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement WiFiDirectHandlerAccessor");
        }
    }

    private void updateToggles() {
        // Set state of Switches and Buttons on load
        Log.i(TAG, "Updating toggle switches");
        final boolean isWifiEnabled = wifiDirectHandler.isWifiEnabled();
        toggleWifiSwitch.setChecked(isWifiEnabled);
        serviceRegistrationSwitch.setEnabled(isWifiEnabled);
        noPromptServiceRegistrationSwitch.setEnabled(isWifiEnabled);
        discoverServicesButton.setEnabled(isWifiEnabled);
    }

    public void handleWifiStateChanged() {
        if (toggleWifiSwitch != null) {
            if (wifiDirectHandler.isWifiEnabled()) {
                serviceRegistrationSwitch.setEnabled(true);
                discoverServicesButton.setEnabled(true);
            } else {
                serviceRegistrationSwitch.setChecked(false);
                serviceRegistrationSwitch.setEnabled(false);
                discoverServicesButton.setEnabled(false);
            }
        }
    }
}
