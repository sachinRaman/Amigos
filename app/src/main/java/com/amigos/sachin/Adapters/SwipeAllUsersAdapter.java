package com.amigos.sachin.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amigos.sachin.AllUsersFragments.AllUsersFragment;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.VO.UserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sachin on 8/27/2017.
 */

public class SwipeAllUsersAdapter extends FragmentStatePagerAdapter {
    public SwipeAllUsersAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(final int position) {

        final Fragment fragment = new AllUsersFragment();

        ArrayList<UserVO> userVOArrayList = ApplicationCache.userVOArrayList;



        Bundle bundle = new Bundle();
        bundle.putSerializable("userData",userVOArrayList.get(position));
        //bundle.putString("id",userVOArrayList[position].getId());
        bundle.putInt("message",position + 1);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public int getCount() {
        return 20;
    }
}
