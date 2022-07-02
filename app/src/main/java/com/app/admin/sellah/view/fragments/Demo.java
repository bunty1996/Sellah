package com.app.admin.sellah.view.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.admin.sellah.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Demo extends Fragment {
  RecyclerView recyclerView_liveChat;
  Button btnSend_liveModule;

    public Demo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_demo, container, false);

        btnSend_liveModule = view.findViewById(R.id.btnSend_liveModule);

        btnSend_liveModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });



        return view;
    }


}
