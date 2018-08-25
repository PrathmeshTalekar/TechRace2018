package com.techrace.spit.techrace2018;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.techrace.spit.techrace2018.HomeFragment.UID;
import static com.techrace.spit.techrace2018.MainActivity.points;
import static com.techrace.spit.techrace2018.LeaderboardActivity.selectUser;
import static com.techrace.spit.techrace2018.MainActivity.selectUID;


public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    ArrayList<LBUpdate> leaderboardItems;
    Context context;
    int c;
    DatabaseReference powerReference;

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
    public void onBindViewHolder(@NonNull final LeaderboardAdapter.LeaderboardViewHolder holder, int position) {
        final int level = leaderboardItems.get(position).level;
        final String name = leaderboardItems.get(position).name;
        final int points = leaderboardItems.get(position).points;
        final String uid = leaderboardItems.get(position).uid;
        final int cool = leaderboardItems.get(position).cool;
        if (cool != 0) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.hotRed));
        }
        holder.positionText.setText("" + String.valueOf(position + 1));
        holder.nameText.setText("" + leaderboardItems.get(position).name);
        holder.levelText.setText("" + leaderboardItems.get(position).level);
        holder.pointsText.setText("" + leaderboardItems.get(position).points);
        Log.i("SELECT USER BOOL", "" + selectUser);
        if (selectUser) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("HI", "onclick");
                    MainActivity.selectUID = uid;
                    Log.i("value of uid", MainActivity.selectUID);
                    if (MainActivity.selectUID != null && !selectUID.equals(UID)) {

                        powerReference = FirebaseDatabase.getInstance().getReference();

                        powerReference.child("Users").child(MainActivity.selectUID).child("cooldown").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                c = dataSnapshot.getValue(Integer.class);
                                Log.i("value of points", "" + MainActivity.points);
                                if (c == 0) {
                                    DatabaseReference powerReference1 = FirebaseDatabase.getInstance().getReference();
                                    powerReference1.child("Users").child(MainActivity.selectUID).child("cooldown").setValue(PowerCardsFragment.twoORfour);
                                    if (PowerCardsFragment.twoORfour == 2) {

                                        powerReference1.child("Users").child(UID).child("points")
                                                .setValue(MainActivity.points - AppConstants.plusTwoPrice);
                                        // MainActivity.prefEditor.putInt("Points",points).apply();
                                        powerReference1.child("Users").child(selectUID).child("Applied By").setValue(UID);
                                    } else if (PowerCardsFragment.twoORfour == 4) {
                                        powerReference1.child("Users").child(UID).child("points")
                                                .setValue(MainActivity.points - AppConstants.plusFourPrice);
                                        // MainActivity.prefEditor.putInt("Points",points).apply();
                                    }
                                    Toast.makeText(holder.cardView.getContext(), "Power Card Applied", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(holder.cardView.getContext(), "Already Applied", Toast.LENGTH_SHORT).show();
                                }
                                MainActivity.selectUID = null;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    } else {
                        Toast.makeText(holder.cardView.getContext(), "Power Card Not Applied", Toast.LENGTH_SHORT).show();
                    }
                    ((Activity) context).finish();
                    selectUser = false;
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
