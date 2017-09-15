package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;
import com.amigos.sachin.VO.UserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.ValueEventListener;

public class PeopleWhoLikedMeActivity extends AppCompatActivity {

    static Context context;
    static ListView likedListView;
    static String myId;
    static LikedUsersLVAdapter likedLVAdapter;
    static TextView tv_noAdmirersTextView;
    static UserVO myUserVO;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, SplashScreen2.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_who_liked_me);

        getSupportActionBar().setTitle("My Admirers");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();

        Firebase.setAndroidContext(context);

        likedListView = (ListView) findViewById(R.id.peopleWhoLikedMeListView1);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        tv_noAdmirersTextView = (TextView) findViewById(R.id.tv_noAdmirersTextView1);

        myUserVO = ApplicationCache.myUserVO;

        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.child("users").child(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()){
                    String name, age, place, sex, status, activity1, activity2, activity3, imageUrl;

                    myUserVO.setId(myId);
                    if("people_who_liked_me".equalsIgnoreCase(userData.getKey())){

                        ArrayList<LikedUserVO> peopleWhoLikedMeVOArrayList = new ArrayList<LikedUserVO>();
                        peopleWhoLikedMeVOArrayList.clear();
                        for(DataSnapshot child : userData.getChildren()){
                            String userId = child.getKey();
                            String time = child.getValue().toString();
                            LikedUserVO likedUserVO = new LikedUserVO();
                            likedUserVO.setTime(time);
                            likedUserVO.setId(userId);
                            likedUserVO.setName("");
                            likedUserVO.setImageUrl("");
                            likedUserVO.setStatus("");
                            peopleWhoLikedMeVOArrayList.add(likedUserVO);
                        }

                        Collections.sort(peopleWhoLikedMeVOArrayList, new Comparator<LikedUserVO>() {
                            @Override
                            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                                    return -1;
                                return 1;
                            }
                        });

                        if (peopleWhoLikedMeVOArrayList == null || peopleWhoLikedMeVOArrayList.isEmpty()){
                            likedListView.setVisibility(View.GONE);
                            tv_noAdmirersTextView.setVisibility(View.VISIBLE);
                        }else{
                            likedListView.setVisibility(View.VISIBLE);
                            tv_noAdmirersTextView.setVisibility(View.GONE);
                        }

                        likedLVAdapter = new LikedUsersLVAdapter(context,peopleWhoLikedMeVOArrayList,likedListView);
                        likedListView.setAdapter(likedLVAdapter);

                    }
                    if("interests_list".equalsIgnoreCase(userData.getKey())){
                        ArrayList<String> interests = new ArrayList<String>();
                        interests.clear();
                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();
                            Map<String, String> topicInterests = interest_list.getValue(Map.class);
                            for (String s: topicInterests.keySet()){
                                if("1".equalsIgnoreCase(topicInterests.get(s))){
                                    interests.add(s);
                                }
                            }
                        }
                        myUserVO.setInterests(interests);
                    }
                    if("moods".equalsIgnoreCase(userData.getKey())){
                        for (DataSnapshot children : userData.getChildren()){
                            if("interests".equalsIgnoreCase(children.getKey())){
                                ArrayList<String> myMoodsTags = new ArrayList<String>();
                                for (DataSnapshot snap : children.getChildren()){
                                    if("1".equalsIgnoreCase(snap.getValue().toString())){
                                        myMoodsTags.add(snap.getKey());
                                    }
                                }
                                myUserVO.setMyMoodTags(myMoodsTags);
                            }
                            if("topic".equalsIgnoreCase(children.getKey())){
                                myUserVO.setMoodTopic(children.getValue().toString());
                            }
                            if("mood".equalsIgnoreCase(children.getKey())){
                                myUserVO.setMood(children.getValue().toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        Firebase peopleWhoLikedMeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/");
        peopleWhoLikedMeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()){
                    String name, age, place, sex, status, activity1, activity2, activity3, imageUrl;

                    myUserVO.setId(myId);
                    if("people_who_liked_me".equalsIgnoreCase(userData.getKey())){

                        ArrayList<LikedUserVO> peopleWhoLikedMeVOArrayList = new ArrayList<LikedUserVO>();
                        peopleWhoLikedMeVOArrayList.clear();
                        for(DataSnapshot child : userData.getChildren()){
                            String userId = child.getKey();
                            String time = child.getValue().toString();
                            LikedUserVO likedUserVO = new LikedUserVO();
                            likedUserVO.setTime(time);
                            likedUserVO.setId(userId);
                            likedUserVO.setName("");
                            likedUserVO.setImageUrl("");
                            likedUserVO.setStatus("");
                            peopleWhoLikedMeVOArrayList.add(likedUserVO);
                        }

                        Collections.sort(peopleWhoLikedMeVOArrayList, new Comparator<LikedUserVO>() {
                            @Override
                            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                                    return -1;
                                return 1;
                            }
                        });

                        if (peopleWhoLikedMeVOArrayList == null || peopleWhoLikedMeVOArrayList.isEmpty()){
                            likedListView.setVisibility(View.GONE);
                            tv_noAdmirersTextView.setVisibility(View.VISIBLE);
                        }else{
                            likedListView.setVisibility(View.VISIBLE);
                            tv_noAdmirersTextView.setVisibility(View.GONE);
                        }

                        likedLVAdapter = new LikedUsersLVAdapter(context,peopleWhoLikedMeVOArrayList,likedListView);
                        likedListView.setAdapter(likedLVAdapter);

                    }
                    if("interests_list".equalsIgnoreCase(userData.getKey())){
                        ArrayList<String> interests = new ArrayList<String>();
                        interests.clear();
                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();
                            Map<String, String> topicInterests = interest_list.getValue(Map.class);
                            for (String s: topicInterests.keySet()){
                                if("1".equalsIgnoreCase(topicInterests.get(s))){
                                    interests.add(s);
                                }
                            }
                        }
                        myUserVO.setInterests(interests);
                    }
                    if("moods".equalsIgnoreCase(userData.getKey())){
                        for (DataSnapshot children : userData.getChildren()){
                            if("interests".equalsIgnoreCase(children.getKey())){
                                ArrayList<String> myMoodsTags = new ArrayList<String>();
                                for (DataSnapshot snap : children.getChildren()){
                                    if("1".equalsIgnoreCase(snap.getValue().toString())){
                                        myMoodsTags.add(snap.getKey());
                                    }
                                }
                                myUserVO.setMyMoodTags(myMoodsTags);
                            }
                            if("topic".equalsIgnoreCase(children.getKey())){
                                myUserVO.setMoodTopic(children.getValue().toString());
                            }
                            if("mood".equalsIgnoreCase(children.getKey())){
                                myUserVO.setMood(children.getValue().toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }
}
