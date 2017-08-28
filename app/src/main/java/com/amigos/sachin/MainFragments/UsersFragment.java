package com.amigos.sachin.MainFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amigos.sachin.Adapters.SwipeAllUsersAdapter;
import com.amigos.sachin.R;

/**
 * Created by Sachin on 8/26/2017.
 */

public class UsersFragment extends Fragment {

    ViewPager viewPagerAllUsers;
    Context context;

    public UsersFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_users,container,false);
        viewPagerAllUsers = (ViewPager) view.findViewById(R.id.viewpager_allusers);
        context = getActivity();

        SwipeAllUsersAdapter swipeAllUsersAdapter = new SwipeAllUsersAdapter(getChildFragmentManager());
        viewPagerAllUsers.setAdapter(swipeAllUsersAdapter);

        return view;
    }
}
