package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PeopleILikedActivity extends AppCompatActivity {

    static Context context;
    static ListView likedListView;
    static String myId;
    static LikedUsersLVAdapter likedLVAdapter;
    static TextView tv_noLikesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_iliked);
        context = getApplicationContext();

        likedListView = (ListView) findViewById(R.id.peopleILikedListView1);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        tv_noLikesTextView = (TextView) findViewById(R.id.tv_noAdmiredTextView1);

        reloadPeopleILikedList();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadPeopleILikedList();
    }

    public static void reloadPeopleILikedList() {
        final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
        ArrayList<LikedUserVO> peopleILikedVOArrayList = peopleILikedDAO.getAllPeopleIliked();

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

        if (peopleILikedVOArrayList.isEmpty()){
            likedListView.setVisibility(View.GONE);
            tv_noLikesTextView.setVisibility(View.VISIBLE);
        }else{
            likedListView.setVisibility(View.VISIBLE);
            tv_noLikesTextView.setVisibility(View.GONE);
        }

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleILikedVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
    }
}
