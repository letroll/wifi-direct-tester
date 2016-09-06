package edu.rit.se.crashavoidance.main;

/**
 * Created by letroll on 04/09/16.
 */

public interface CommunicationView {
    void showChatFragment();

    void showTicTacToeFragment();

    void onDeviceChange();

    void onMessageReceived(byte[] byteArrayMessage);

    void onWifiStateChanged();

    void registerCommunicationReceiver();

    void bindWifiServiceConnection();

    void showAvailableServicesFragment();
}
