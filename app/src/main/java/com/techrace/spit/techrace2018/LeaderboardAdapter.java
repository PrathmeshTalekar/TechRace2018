package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.techrace.spit.techrace2018.LeaderboardActivity.selectUser;


public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    ArrayList<LBUpdate> leaderboardItems;
    Context context;

    public LeaderboardAdapter(ArrayList<LBUpdate> leaderboardItems, Context context) {
        this.leaderboardItems = leaderboardItems;
        this.context = context;
    }

    @Override
    public LeaderboardAdapter.LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardAdapter.LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.LeaderboardViewHolder holder, int position) {
        final int level = leaderboardItems.get(position).level;
        final String name = leaderboardItems.get(position).name;
        final int points = leaderboardItems.get(position).points;
        final String uid = leaderboardItems.get(position).uid;
        holder.positionText.setText("" + String.valueOf(position + 1));
        holder.nameText.setText("" + leaderboardItems.get(position).name);
        holder.levelText.setText("" + leaderboardItems.get(position).level);
        holder.pointsText.setText("" + leaderboardItems.get(position).points);
        if (selectUser) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("HI", "onclick");
                    MainActivity.selectUID = uid;
                    ((Activity) context).finish();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return leaderboardItems.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, levelText, positionText, pointsText;
        CardView cardView;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.leaderboard_item_name);
            levelText = (TextView) itemView.findViewById(R.id.leaderboard_item_clues_solved);
            positionText = (TextView) itemView.findViewById(R.id.leaderboard_item_position);
            pointsText = (TextView) itemView.findViewById(R.id.leaderboard_item_applied);

            cardView = (CardView) itemView.findViewById(R.id.leaderboard_item_card);
        }
    }

//public class LeaderboardAdapter extends ArrayAdapter<LBUpdate>{
//
//    int i=1;
//    public LeaderboardAdapter(Activity context, ArrayList<LBUpdate> leaderboard){
//        super(context,0,leaderboard);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View listItemView=convertView;
//        if(listItemView==null){
//            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_item,parent,false);
//        }
//        Log.i("LEADERADAPTER","iNSIDE");
//        LBUpdate lbUpdate=getItem(position);
//        Log.i("LBUPDATE REF",lbUpdate.name);
//        TextView player=(TextView)listItemView.findViewById(R.id.leaderboard_name);
//        player.setText(lbUpdate.name);
//        TextView points=(TextView)listItemView.findViewById(R.id.leaderboardPoints);
//        points.setText(""+lbUpdate.points);
//        TextView level=(TextView)listItemView.findViewById(R.id.leaderboardLevel);
//        level.setText(""+lbUpdate.level);
//        TextView playerPosition=(TextView)listItemView.findViewById(R.id.leaderboard_item_position);
//        playerPosition.setText(""+(position+1));
//
//        return listItemView;
//
//    }


}
