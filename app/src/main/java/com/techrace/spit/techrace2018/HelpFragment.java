package com.techrace.spit.techrace2018;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpFragment extends Fragment {
    View myView;
    TextView event, tech;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView=inflater.inflate(R.layout.help_layout,container,false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        event = myView.findViewById(R.id.eventSupport);
        tech = myView.findViewById(R.id.techSupport);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+917738107772"));
                startActivity(intent);
            }
        });
        tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+919930004241"));
                startActivity(intent);
            }
        });
    }
//    public void numberClicked(View view){
//        //if(view.getId()==R.id.eventSupport){
//            Intent intent=new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse(view.toString()));
//            startActivity(intent);
//      //  }
//    }
}
