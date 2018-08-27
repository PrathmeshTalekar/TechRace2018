package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    Context context;
    ArrayList<Feed> feedArrayList;

    public FeedAdapter(ArrayList<Feed> feedArrayList, Context context) {

        this.feedArrayList = feedArrayList;
        this.context = context;
    }

    @Override
    public FeedAdapter.FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
        return new FeedAdapter.FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        String title = feedArrayList.get(position).getmTitle();
        String info = feedArrayList.get(position).getmInfo();
        holder.titleText.setText(title);
        holder.infoText.setText(info);
    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, infoText;
        CardView cardView;

        public FeedViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.title);
            infoText = (TextView) itemView.findViewById(R.id.info);
            cardView = (CardView) itemView.findViewById(R.id.feedItemCard);
        }
    }
}
