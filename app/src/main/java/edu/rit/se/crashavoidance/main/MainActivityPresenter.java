package edu.rit.se.crashavoidance.main;

import android.util.Log;

import javax.inject.Inject;

import edu.rit.se.crashavoidance.infrastructure.di.annotation.PerActivity;

/**
 * Created by letroll on 04/09/16.
 */

@PerActivity
public class MainActivityPresenter {

    public static final String TAG = MainActivityPresenter.class.getSimpleName();

    private CommunicationView communicationView;

    @Inject
    public MainActivityPresenter() {
    }

    public void setView(final CommunicationView communicationView) {
        this.communicationView = communicationView;
    }

    public void onCreate() {
        communicationView.registerCommunicationReceiver();
        Log.i(TAG, "MainActivity created");

        communicationView.bindWifiServiceConnection();
    }
}
