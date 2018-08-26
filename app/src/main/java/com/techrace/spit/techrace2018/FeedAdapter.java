package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedAdapter extends ArrayAdapter<Feed> {
    public FeedAdapter(Activity context, ArrayList<Feed> feed) {
        super(context, 0, feed);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
        }
        Feed currentFeed = getItem(position);
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(currentFeed.getmTitle());
        TextView infoTextView = (TextView) listItemView.findViewById(R.id.info);
        infoTextView.setText(currentFeed.getmInfo());
        return listItemView;
    }
}
