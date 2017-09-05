package com.amigos.sachin.MainFragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.amigos.sachin.ChatsFragments.MyChatFragment;
import com.amigos.sachin.ChatsFragments.PeopleILikedFragment;
import com.amigos.sachin.ChatsFragments.PeopleWhoLikedMeFragment;
import com.amigos.sachin.CustomViews.CustomViewPager;
import com.amigos.sachin.R;

/**
 * Created by Sachin on 8/26/2017.
 */

public class ChatsFragment extends Fragment {

    BottomNavigationView myChatsBottomNavigationView;
    CustomViewPager myChatsViewPager;
    private FragmentActivity myContext;

    public ChatsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_chats,container,false);
        myChatsViewPager = (CustomViewPager) view.findViewById(R.id.my_chats_viewpager);
        myChatsViewPager.setPagingEnabled(false);
        myChatsViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        //Comment below code to enable swiping but dont forget to select the bottom navigation
        myChatsViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        myChatsBottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation_my_chats);
        myChatsBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.my_chats:
                        myChatsViewPager.setCurrentItem(0);
                        return true;
                    case R.id.my_likes:
                        myChatsViewPager.setCurrentItem(1);
                        return true;
                    case R.id.my_liked:
                        myChatsViewPager.setCurrentItem(2);
                        return true;
                    default:
                        myChatsViewPager.setCurrentItem(0);
                        return true;
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return MyChatFragment.newInstance("My Chats");
                case 1: return PeopleILikedFragment.newInstance("People I liked");
                case 2: return PeopleWhoLikedMeFragment.newInstance("People who liked me");
                default: return MyChatFragment.newInstance("My Chats, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
