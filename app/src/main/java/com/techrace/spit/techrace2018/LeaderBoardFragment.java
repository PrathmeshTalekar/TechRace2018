package com.techrace.spit.techrace2018;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class LeaderBoardFragment extends Fragment {
    public LeaderBoardFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leaderboard_layout, container, false);
        final ArrayList<Leaderboard> leaderboard = new ArrayList<Leaderboard>();
        leaderboard.add(new Leaderboard("ram and sham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram andsham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram and sham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram andsham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram and sham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram andsham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram and sham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));
        leaderboard.add(new Leaderboard("ram andsham", 10, 100));
        leaderboard.add(new Leaderboard("pammer and charotan", 9, 90));

        LeaderboardAdapter adapter = new LeaderboardAdapter(getActivity(), leaderboard);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return rootView;
    }
}
