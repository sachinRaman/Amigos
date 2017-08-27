package com.amigos.sachin.ChatsFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amigos.sachin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleWhoLikedMeFragment extends Fragment {


    public PeopleWhoLikedMeFragment() {
        // Required empty public constructor
    }

    public static PeopleWhoLikedMeFragment newInstance(String param1) {
        PeopleWhoLikedMeFragment fragment = new PeopleWhoLikedMeFragment();
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

        View view =  inflater.inflate(R.layout.fragment_people_who_liked_me, container, false);
        return view;
    }

}
