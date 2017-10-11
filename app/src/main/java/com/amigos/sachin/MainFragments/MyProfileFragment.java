package com.amigos.sachin.MainFragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.amigos.sachin.CustomViews.CustomViewPager;
import com.amigos.sachin.MyProfileFragments.MyInfoFragment;
import com.amigos.sachin.MyProfileFragments.MyInterestsFragment;
import com.amigos.sachin.MyProfileFragments.MyMoods;
import com.amigos.sachin.R;

import java.util.List;

/**
 * Created by Sachin on 8/26/2017.
 */

public class MyProfileFragment extends Fragment {

    BottomNavigationView myProfileBottomNavigationView;
    CustomViewPager myProfileViewPager;
    private FragmentActivity myContext;
    Context context;
    MenuItem prevMenuItem;

    public MyProfileFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view =  inflater.inflate(R.layout.fragment_main_my_profile,container,false);
        myProfileViewPager = (CustomViewPager) view.findViewById(R.id.my_profile_viewpager);
        /*myProfileViewPager.setPagingEnabled(false);*/
        myProfileViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        myProfileBottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation_my_profile);
        myProfileBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.my_mood:
                        myProfileViewPager.setCurrentItem(0);
                        return true;
                    case R.id.my_profile:
                        myProfileViewPager.setCurrentItem(1);
                        return true;
                    case R.id.my_interests:
                        myProfileViewPager.setCurrentItem(2);
                        return true;
                    default:
                        myProfileViewPager.setCurrentItem(0);
                        return true;
                }
            }
        });
        prevMenuItem = myProfileBottomNavigationView.getMenu().getItem(0);
        myProfileViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    myProfileBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                myProfileBottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = myProfileBottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                case 0: return MyMoods.newInstance("My Moods");
                case 1: return MyInfoFragment.newInstance("My Info");
                case 2: return MyInterestsFragment.newInstance("My Interests");
                default: return MyMoods.newInstance("My Moods, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
