package com.amigos.sachin.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amigos.sachin.AllUsersFragments.AllUsersFragment;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.Utils.Algorithms;
import com.amigos.sachin.VO.UserVO;
import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
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
    String myId;
    public static ArrayList<UserVO> userVOArrayList = new ArrayList<UserVO>();

    @Override
    public Fragment getItem(final int position) {

        final Fragment fragment = new AllUsersFragment();

        UserVO myUserVO = ApplicationCache.myUserVO;
        myId = myUserVO.getId();
        /*userVOArrayList.clear();*/
        userVOArrayList = ApplicationCache.userVOArrayList;
        ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        for( int i = userVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = userVOArrayList.get(i).getId();
            if( peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId)
                    || myUserVO.getPeopleIRemoved().contains(userId)){
                userVOArrayList.remove(i);
            }
        }



        for(UserVO userVO : userVOArrayList){
            int match = 0;
            ArrayList<ArrayList<String>> myInterests = new ArrayList<ArrayList<String>>(Arrays.asList(myUserVO.getInterests1(),
                    myUserVO.getInterests2(), myUserVO.getInterests3()));
            ArrayList<ArrayList<String>> userInterests = new ArrayList<ArrayList<String>>(Arrays.asList(userVO.getInterests1(),
                    userVO.getInterests2(), userVO.getInterests3()));
            match = Algorithms.calculateMatch(myInterests, userInterests);
            userVO.setMatch(match);
        }


        if("1".equalsIgnoreCase(myUserVO.getMood())){
            if(myUserVO.getMyMoodTags() != null && !myUserVO.getMyMoodTags().isEmpty()){
                for(UserVO userVO : userVOArrayList){
                    int matchCount = 0;
                    for(String s : myUserVO.getMyMoodTags()){
                        if(userVO.getInterests1().contains(s)){
                            matchCount += 300;
                        }
                        if(userVO.getInterests2().contains(s)){
                            matchCount += 200;
                        }
                        if(userVO.getInterests3().contains(s)){
                            matchCount += 100;
                        }
                    }
                    int numMoodTags = 3*myUserVO.getMyMoodTags().size();

                    int myRequirementMatch = matchCount/numMoodTags;
                    int personalityMatch = userVO.getMatch();

                    int finalMatch = 0;
                    if(myRequirementMatch > personalityMatch){
                        finalMatch = myRequirementMatch + (personalityMatch/2);
                    }else if(myRequirementMatch < personalityMatch){
                        finalMatch = (myRequirementMatch + personalityMatch)/2;
                    }else{
                        finalMatch = myRequirementMatch;
                    }

                    if(finalMatch > 100){
                        userVO.setMatch(100);
                    }else {
                        userVO.setMatch(finalMatch);
                    }
                }
            }
        }

        Collections.sort(userVOArrayList, new Comparator<UserVO>() {
            @Override
            public int compare(UserVO lhs, UserVO rhs) {
                if ( lhs.getMatch() > rhs.getMatch()) {
                    return -1;
                }else if (lhs.getMatch() < rhs.getMatch()){
                    return 1;
                }
                return 0;
            }
        });



        Bundle bundle = new Bundle();
        bundle.putSerializable("userData",userVOArrayList.get(position));
        //bundle.putString("id",userVOArrayList[position].getId());
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public int getCount() {
        return 40;
    }
}
