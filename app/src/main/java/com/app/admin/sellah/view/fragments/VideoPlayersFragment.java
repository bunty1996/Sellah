package com.app.admin.sellah.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.admin.sellah.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoPlayersFragment extends Fragment {

    Unbinder unbinder;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_view_players, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        String uri ="" ;
        if(getArguments()!=null){
            Bundle bundle=getArguments().getBundle("url_bundle");
            if (bundle!=null){
                uri=bundle.getString("url");
            }

        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }
}
