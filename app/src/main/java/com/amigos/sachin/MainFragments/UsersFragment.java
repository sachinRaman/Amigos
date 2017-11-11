package com.amigos.sachin.MainFragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.Activities.MyMoodActivity;
import com.amigos.sachin.Adapters.SwipeAllUsersAdapter;
import com.amigos.sachin.AllUsersFragments.LoadingAllUsersFragment;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.MyProfileFragments.MyMoods;
import com.amigos.sachin.R;

import java.util.HashSet;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Sachin on 8/26/2017.
 */

public class UsersFragment extends Fragment {

    static ViewPager viewPagerAllUsers;
    Context context;
    static FragmentManager fm;
    public static SwipeAllUsersAdapter swipeAllUsersAdapter;
    TextView changePreferenceButton;
    //BottomNavigationView myProfileBottomNavigationView;

    public UsersFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_users,container,false);
        context = getActivity();

        viewPagerAllUsers = (ViewPager) view.findViewById(R.id.viewpager_allusers);
        changePreferenceButton = (TextView) view.findViewById(R.id.changePreferenceButton);
        //myProfileBottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation_my_profile);
        fm = getChildFragmentManager();

        changePreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyMoodActivity.class);
                startActivity(intent);
            }
        });

        viewPagerAllUsers.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        startHeavyProcessing();


        //reloadAllUsers();


        return view;
    }

    public static void reloadAllUsers() {
        swipeAllUsersAdapter = new SwipeAllUsersAdapter(fm);
        viewPagerAllUsers.setAdapter(swipeAllUsersAdapter);
        viewPagerAllUsers.setCurrentItem(0);
    }

    public static void updateSwipeUsersAdapter(int position){
        swipeAllUsersAdapter.notifyDataSetChanged();
        viewPagerAllUsers.setAdapter(swipeAllUsersAdapter);
        viewPagerAllUsers.setCurrentItem(position);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                //case 0: return MyMoods.newInstance("Preferences");
                case 0: return LoadingAllUsersFragment.newInstance("Loading all users");
                default: return LoadingAllUsersFragment.newInstance("Loading all users, Default");
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    private void startHeavyProcessing(){
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String

            /*sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
            myId = sp.getString("myId", "");

            ApplicationCache.setMyId(myId);
            ApplicationCache applicationCache = new ApplicationCache();*/


            /*for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }*/
            while(ApplicationCache.userVOArrayList.isEmpty()){
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (flag == 0) {
                            flag = 1;
                            Toast.makeText(context, "You have poor internet connection.\nLoading the data might take some time.", 3000).show();
                        }
                    }
                });*/
                /*ToastHandler mToastHandler = new ToastHandler(context);
                mToastHandler.showToast("Internet connection is poor",1000);*/
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }

            updateSharedPreferences();

            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            /*progress.dismiss();*/
            reloadAllUsers();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void updateSharedPreferences(){
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        Set<String> set = new HashSet<String>();
        Set<String> interests1 = new HashSet<String>();
        Set<String> interests2 = new HashSet<String>();
        Set<String> interests3 = new HashSet<String>();
        Set<String> pendingChatRequests = new HashSet<String>();
        Set<String> approvedChatRequests = new HashSet<String>();
        Set<String> sentChatRequests = new HashSet<String>();

        Set set1 = new HashSet<String>();
        set1.addAll(ApplicationCache.peopleIBlockedList);
        set1.addAll(ApplicationCache.peopleWhoBlockedMeList);

        if(ApplicationCache.myUserVO.getInterests() != null) {
            set.addAll(ApplicationCache.myUserVO.getInterests());
        }
        if(ApplicationCache.myUserVO.getInterests1() != null) {
            interests1.addAll(ApplicationCache.myUserVO.getInterests1());
        }
        if(ApplicationCache.myUserVO.getInterests2() != null) {
            interests2.addAll(ApplicationCache.myUserVO.getInterests2());
        }
        if(ApplicationCache.myUserVO.getInterests3() != null) {
            interests3.addAll(ApplicationCache.myUserVO.getInterests3());
        }
        if(ApplicationCache.myUserVO.getPendingChatRequests() != null) {
            pendingChatRequests.addAll(ApplicationCache.myUserVO.getPendingChatRequests());
        }
        if(ApplicationCache.myUserVO.getApprovedChatRequests() != null) {
            approvedChatRequests.addAll(ApplicationCache.myUserVO.getApprovedChatRequests());
        }
        sp.edit().putStringSet("interests", set)
                .putStringSet("interests1", interests1)
                .putStringSet("interests2", interests2)
                .putStringSet("interests3", interests3)
                .putStringSet("approved_requests", approvedChatRequests)
                .putStringSet("pending_requests", pendingChatRequests)
                .putStringSet("sent_requests", sentChatRequests)
                .putString("name",ApplicationCache.myUserVO.getName())
                .putStringSet("blocked",set1)
                .apply();
    }

}
