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
import java.util.Collections;
import java.util.Comparator;
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

        UserVO myUserVO = ApplicationCache.myUserVO;
        ArrayList<UserVO> userVOArrayList = ApplicationCache.userVOArrayList;
        for(UserVO userVO : userVOArrayList){
            if(myUserVO.getInterests() != null && !myUserVO.getInterests().isEmpty()){
                int matchCount = 0;
                for(String s : userVO.getInterests()){
                    if(myUserVO.getInterests().contains(s)){
                        matchCount++;
                    }
                }
                userVO.setMatch(Math.round(((float)matchCount/(myUserVO.getInterests().size()))*100));
            }
        }
        if("1".equalsIgnoreCase(myUserVO.getMood())){
            if(myUserVO.getMyMoodTags() != null && !myUserVO.getMyMoodTags().isEmpty()){
                for(UserVO userVO : userVOArrayList){
                    if("1".equalsIgnoreCase(userVO.getMood())){
                        int matchCount = 0;
                        for(String s : userVO.getMyMoodTags()){
                            if(myUserVO.getMyMoodTags().contains(s)){
                                matchCount++;
                            }
                        }
                        userVO.setMatch(userVO.getMatch() + matchCount*100);
                    }
                }
            }
        }

        Collections.sort(userVOArrayList, new Comparator<UserVO>() {
            @Override
            public int compare(UserVO lhs, UserVO rhs) {
                if ( lhs.getMatch() > rhs.getMatch())
                    return -1;
                return 1;
            }
        });



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
