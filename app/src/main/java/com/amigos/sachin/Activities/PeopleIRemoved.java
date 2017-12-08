package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PeopleIRemoved extends AppCompatActivity {

    static Context context;
    static ListView likedListView;
    static String myId;
    static LikedUsersLVAdapter likedLVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_iremoved);

        context = getApplicationContext();

        likedListView = (ListView) findViewById(R.id.peopleIRemovedListView);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        reloadPeopleIRemovedList();

    }

    public static void reloadPeopleIRemovedList() {

        ArrayList<LikedUserVO> peopleILikedVOArrayList = ApplicationCache.peopleIRemovedVOArrayList;

        ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        for( int i = peopleILikedVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = peopleILikedVOArrayList.get(i).getId();
            if( peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId) ){
                peopleILikedVOArrayList.remove(i);
            }
        }
        Collections.sort(peopleILikedVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleILikedVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadPeopleIRemovedList();
    }
}
