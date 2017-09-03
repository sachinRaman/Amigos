package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.amigos.sachin.Adapters.BlockListArrayAdapter;
import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BlockListActivity extends AppCompatActivity {

    static Context context;
    static ListView blockedListView;
    static String myId;
    static BlockListArrayAdapter blockListArrayAdapter;
    ArrayList<LikedUserVO> peopleIBlockedVOList = new ArrayList<LikedUserVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);

        context = getApplicationContext();
        blockedListView = (ListView) findViewById(R.id.blockListView);
        SharedPreferences sp = getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        //peopleIBlockedVOList = ApplicationCache.peopleIBlockedVOList;

        Firebase myBlockListRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/block_list/people_i_blocked/");
        myBlockListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleIBlockedVOList.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    LikedUserVO likedUserVO = new LikedUserVO();
                    likedUserVO.setId(child.getKey().toString());
                    likedUserVO.setTime(child.getValue().toString());
                    peopleIBlockedVOList.add(likedUserVO);
                }
                Collections.sort(peopleIBlockedVOList, new Comparator<LikedUserVO>() {
                    @Override
                    public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                        if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                            return -1;
                        return 1;
                    }
                });

                blockListArrayAdapter = new BlockListArrayAdapter(context,peopleIBlockedVOList,blockedListView);
                blockedListView.setAdapter(blockListArrayAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
