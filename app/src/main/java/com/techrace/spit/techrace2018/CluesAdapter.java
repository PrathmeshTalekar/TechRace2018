package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CluesAdapter extends ArrayAdapter<String> {
    public CluesAdapter(Activity context, ArrayList<String> clue) {
        super(context, 0, clue);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.clues_item, parent, false);
        }
        String currentString = getItem(position);
        TextView clueTextView = (TextView) listItemView.findViewById(R.id.clue_text);
        clueTextView.setText(currentString);
        TextView positionTextView = (TextView) listItemView.findViewById(R.id.clue_number);
        positionTextView.setText(""+(position+1));
        return listItemView;
    }
}
