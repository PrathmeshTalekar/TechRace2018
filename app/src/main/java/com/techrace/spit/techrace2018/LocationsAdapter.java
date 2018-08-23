package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationsAdapter extends ArrayAdapter<Location> {
    public LocationsAdapter(Activity context, ArrayList<Location> location) {
        super(context, 0, location);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.locations_item, parent, false);
        }
        Location currentLocation = getItem(position);
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.location_text);
        locationTextView.setText(currentLocation.getmLocation());
        TextView positionTextView = (TextView) listItemView.findViewById(R.id.location_number);
        positionTextView.setText(""+(position+1));
        return listItemView;
    }
}
