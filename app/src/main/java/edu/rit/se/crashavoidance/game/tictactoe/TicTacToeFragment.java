package edu.rit.se.crashavoidance.game.tictactoe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.rit.se.crashavoidance.R;
import edu.rit.se.crashavoidance.infrastructure.BaseFragment;
import edu.rit.se.crashavoidance.infrastructure.WiFiDirectHandlerAccessor;
import edu.rit.se.crashavoidance.infrastructure.network.Message;
import edu.rit.se.crashavoidance.infrastructure.network.MessageType;
import edu.rit.se.wifibuddy.CommunicationManager;
import edu.rit.se.wifibuddy.WifiDirectHandler;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with a send button.
 */
public class TicTacToeFragment extends BaseFragment {

    public static final String TAG = WifiDirectHandler.TAG + "TicTacToeFragment";

    @BindView(R.id.btn_ciseaux)
    Button btnCiseaux;
    @BindView(R.id.btn_pierre)
    Button btnPierre;
    @BindView(R.id.btn_feuille)
    Button btnFeuille;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_victory)
    TextView tvVictory;

    private WiFiDirectHandlerAccessor handlerAccessor;
    private TicTacToeChoise otherPlayerchoise = null;
    private TicTacToeChoise playerChoise = null;
    private int victoryCount=0;
    private int defeatCount=0;
    private int equalityCount=0;

    public static TicTacToeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TicTacToeFragment fragment = new TicTacToeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tictactoe, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_ciseaux)
    void sendCiseau() {
        sendMessage(TicTacToeChoise.CISEAU);
    }

    @OnClick(R.id.btn_feuille)
    void sendFeuille() {
        sendMessage(TicTacToeChoise.FEUILLE);
    }

    @OnClick(R.id.btn_pierre)
    void sendPierre() {
        sendMessage(TicTacToeChoise.PIERRE);
    }

    private void sendMessage(final TicTacToeChoise ticTacToeChoise) {
        if (ticTacToeChoise == null) return;
        Log.e(WifiDirectHandler.TAG, ticTacToeChoise.name() + " button tapped");
        playerChoise = ticTacToeChoise;
        CommunicationManager communicationManager = handlerAccessor.getWifiHandler().getCommunicationManager();
        if (communicationManager != null) {
            byte[] messageBytes = (getAuthorName() + ": " + ticTacToeChoise).getBytes();
            Message finalMessage = new Message(MessageType.TICTACTOE, messageBytes);
            communicationManager.write(SerializationUtils.serialize(finalMessage));
        } else {
            Log.e(TAG, "Communication Manager is null");
        }

        switch (ticTacToeChoise) {
            case PIERRE:
                btnFeuille.setEnabled(false);
                btnFeuille.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                btnCiseaux.setEnabled(false);
                btnCiseaux.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case FEUILLE:
                btnPierre.setEnabled(false);
                btnPierre.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                btnCiseaux.setEnabled(false);
                btnCiseaux.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case CISEAU:
            default:
                btnPierre.setEnabled(false);
                btnPierre.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                btnFeuille.setEnabled(false);
                btnFeuille.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
        }
        checkIfWeHaveAWinner();
    }

    private String getAuthorName() {
        return handlerAccessor.getWifiHandler().getThisDevice().deviceName.split(" ")[0];
    }

    public void pushMessage(byte[] readMessage) {
        Message message = SerializationUtils.deserialize(readMessage);
        switch (message.messageType) {
            case TICTACTOE:
            default:
                if (otherPlayerchoise != null && playerChoise != null) {
                    reset();
                }
                final String mes = new String(message.message);
                tvStatus.setText("L'autre joueur à joué");
                otherPlayerchoise = getOtherPlayerchoise(mes);
                checkIfWeHaveAWinner();
                break;
        }
    }

    private void reset() {
        btnCiseaux.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        btnFeuille.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        btnPierre.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        btnPierre.setEnabled(true);
        btnFeuille.setEnabled(true);
        btnCiseaux.setEnabled(true);
        otherPlayerchoise = null;
        playerChoise = null;
        tvStatus.setText("");
    }

    private TicTacToeChoise getOtherPlayerchoise(final String mes) {
        if (mes != null && mes.contains(": ")) {
            String[] parts = mes.split(": ");
            if (parts.length == 2) {
                final TicTacToeChoise otherPlayerChoice = TicTacToeChoise.fromString(parts[1]);
                final String author = parts[0];
                final String aut = getAuthorName();
                if (!aut.equals(author))
                    return otherPlayerChoice;
            }
        }
        return null;
    }

    private void checkIfWeHaveAWinner() {
        if (otherPlayerchoise == null || playerChoise == null) {
            return;
        }

        final boolean lose = otherPlayerchoise == TicTacToeChoise.PIERRE && playerChoise == TicTacToeChoise.CISEAU ||
                otherPlayerchoise == TicTacToeChoise.CISEAU && playerChoise == TicTacToeChoise.FEUILLE ||
                otherPlayerchoise == TicTacToeChoise.FEUILLE && playerChoise == TicTacToeChoise.PIERRE;
        final boolean equality = otherPlayerchoise == playerChoise;
        showResult(lose, equality);
    }

    private void showResult(boolean lose, boolean equality) {
        if (equality) {
            Toast.makeText(getActivity(), "égalité", Toast.LENGTH_SHORT).show();
            equalityCount++;
        } else {
            Toast.makeText(getActivity(), "tu as " + (lose ? "perdu" : "gagné"), Toast.LENGTH_SHORT).show();
            if(lose){
                defeatCount++;
            }else{
                victoryCount++;
            }
        }
        tvVictory.setText("égalité:"+equalityCount + " défaite:"+defeatCount+ " victoire:"+victoryCount);
        reset();
    }

    /**
     * This is called when the Fragment is opened and is attached to MainActivity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            handlerAccessor = ((WiFiDirectHandlerAccessor) getActivity());
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement WiFiDirectHandlerAccessor");
        }
    }
}
