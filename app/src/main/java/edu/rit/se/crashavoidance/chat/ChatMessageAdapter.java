package edu.rit.se.crashavoidance.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * ArrayAdapter to manage chat messages.
 */
public class ChatMessageAdapter extends ArrayAdapter<String> {

    private final List<String> items;

    public ChatMessageAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_list_item_1, null);
        }
        String message = items.get(position);
        if (message != null && !message.isEmpty()) {
            TextView nameText = (TextView) v.findViewById(android.R.id.text1);
            if (nameText != null) {
                nameText.setText(message);
                if (message.startsWith("Me: ")) {
                    // My message
                    nameText.setGravity(Gravity.RIGHT);
                } else {
                    // Buddy's message
                    nameText.setGravity(Gravity.LEFT);
                }
            }
        }
        return v;
    }
}