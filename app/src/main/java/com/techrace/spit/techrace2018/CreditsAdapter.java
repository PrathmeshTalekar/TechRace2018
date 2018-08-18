package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CreditsAdapter extends ArrayAdapter<Core> {


    public CreditsAdapter(Activity context, ArrayList<Core> core) {
        super(context, 0, core);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.core_item, parent, false);
        }
        Core currentCore = getItem(position);
        TextView coreTextView = (TextView) listItemView.findViewById(R.id.coreName);
        coreTextView.setText(currentCore.getmName());
        TextView positionTextView = (TextView) listItemView.findViewById(R.id.corePosition);
        positionTextView.setText(currentCore.getmPosition());
        return listItemView;
    }
}
