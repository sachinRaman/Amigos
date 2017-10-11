package com.amigos.sachin.MainFragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amigos.sachin.Adapters.SwipeAllUsersAdapter;
import com.amigos.sachin.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Sachin on 8/26/2017.
 */

public class UsersFragment extends Fragment {

    static ViewPager viewPagerAllUsers;
    Context context;
    static FragmentManager fm;
    public static SwipeAllUsersAdapter swipeAllUsersAdapter;

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
        fm = getChildFragmentManager();

        reloadAllUsers();

        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);




        //Yes Button
        *//*builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Yes button Clicked", Toast.LENGTH_LONG).show();
                Log.i("Code2care ", "Yes button Clicked!");
                dialog.dismiss();
            }
        });*//*




        *//*LayoutInflater inflater = getLayoutInflater();*//*
        LayoutInflater inflater1 = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialoglayout = inflater1.inflate(R.layout.custom_dialog, null);

        builder.setView(dialoglayout);
        builder.show();*/

        return view;
    }

    public void reloadAllUsers() {
        swipeAllUsersAdapter = new SwipeAllUsersAdapter(fm);
        viewPagerAllUsers.setAdapter(swipeAllUsersAdapter);
        viewPagerAllUsers.setCurrentItem(0);
    }

    public static void updateSwipeUsersAdapter(int position){
        swipeAllUsersAdapter.notifyDataSetChanged();
        viewPagerAllUsers.setAdapter(swipeAllUsersAdapter);
        viewPagerAllUsers.setCurrentItem(position);
    }

}
