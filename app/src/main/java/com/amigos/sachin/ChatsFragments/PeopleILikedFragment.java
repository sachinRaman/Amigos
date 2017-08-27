package com.amigos.sachin.ChatsFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amigos.sachin.R;

public class PeopleILikedFragment extends Fragment {


    public PeopleILikedFragment() {

    }

    public static PeopleILikedFragment newInstance(String param1) {
        PeopleILikedFragment fragment = new PeopleILikedFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people_iliked, container, false);
        return view;
    }

}
