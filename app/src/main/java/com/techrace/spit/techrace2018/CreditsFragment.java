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

public class CreditsFragment extends Fragment {
    public CreditsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.credits_layout, container, false);
        final ArrayList<Core> core = new ArrayList<Core>();
        core.add(new Core("Aman Agrawal", "Tech Head"));
        core.add(new Core("Prathmesh Talekar", "Executive Head"));
        core.add(new Core("Yashvi Desai", "Event Head"));
        core.add(new Core("Sahil Sheth", "Marketing Head"));
        core.add(new Core("Raksha Jain", "Creative Head"));
        core.add(new Core("Sarah Hawa", "Head Of Operations"));
        core.add(new Core("Sunit Vaidya", "PR Head"));
        core.add(new Core("Taksh Soni", "Admin & Finance Head"));

        CreditsAdapter adapter = new CreditsAdapter(getActivity(), core);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        return rootView;
    }
}
