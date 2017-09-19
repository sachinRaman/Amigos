package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatUsersVO;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ChatListActivity extends AppCompatActivity {

    static Context context;
    static ListView chatListView;
    static String myId;
    static ChatLVAdapter chatLVAdapter;
    static TextView tv_emptyChat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        getSupportActionBar().setTitle("My Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();
        Firebase.setAndroidContext(context);
        ApplicationCache.loadMyUserVO();




        chatListView = (ListView) findViewById(R.id.chatListView1);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        tv_emptyChat = (TextView) findViewById(R.id.tv_emptyChat1);

        Firebase.setAndroidContext(context);
        Firebase interestsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/interests_list/");
        interestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> interests = new ArrayList<String>();
                for (DataSnapshot interest_list : dataSnapshot.getChildren()){
                    String key = interest_list.getKey();
                    for(DataSnapshot thisTopicOfInterest : interest_list.getChildren()){
                        if("1".equalsIgnoreCase(thisTopicOfInterest.getValue().toString())){
                            interests.add(thisTopicOfInterest.getKey());
                        }
                    }
                    /*Map<String, String> topicInterests = interest_list.getValue(Map.class);
                    for (String s: topicInterests.keySet()){
                        if("1".equalsIgnoreCase(topicInterests.get(s))){
                            interests.add(s);
                        }
                    }*/
                }
                ApplicationCache.myUserVO.setInterests(interests);
                loadChatData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.child("users").child(myId).child("interests_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> interests = new ArrayList<String>();
                for (DataSnapshot interest_list : dataSnapshot.getChildren()){
                    String key = interest_list.getKey();
                    for(DataSnapshot thisTopicOfInterest : interest_list.getChildren()){
                        if("1".equalsIgnoreCase(thisTopicOfInterest.getValue().toString())){
                            interests.add(thisTopicOfInterest.getKey());
                        }
                    }
                    *//*Map<String, String> topicInterests = interest_list.getValue(Map.class);
                    for (String s: topicInterests.keySet()){
                        if("1".equalsIgnoreCase(topicInterests.get(s))){
                            interests.add(s);
                        }
                    }*//*
                }
                ApplicationCache.myUserVO.setInterests(interests);
                loadChatData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }

    private static void loadChatData() {
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        ArrayList<ChatUsersVO> chatUsersVOArrayList = chatUsersDAO.getMyChatList(myId);
        SharedPreferences sp;
        sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        Set<String> blockedUsersSet = sp.getStringSet("blocked", new HashSet<String>());
        ArrayList<String> blockedUsers = new ArrayList<String>();
        blockedUsers.addAll(blockedUsersSet);

        for( int i = chatUsersVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = chatUsersVOArrayList.get(i).getUserId();
            if( blockedUsers.contains(userId) || myId.equalsIgnoreCase(userId) ){
                chatUsersVOArrayList.remove(i);
            }
        }

        Collections.sort(chatUsersVOArrayList, new Comparator<ChatUsersVO>() {
            @Override
            public int compare(ChatUsersVO lhs, ChatUsersVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        if (chatUsersVOArrayList.isEmpty()){
            chatListView.setVisibility(View.GONE);
            tv_emptyChat.setVisibility(View.VISIBLE);
        }else{
            chatListView.setVisibility(View.VISIBLE);
            tv_emptyChat.setVisibility(View.GONE);
        }

        chatLVAdapter = new ChatLVAdapter(context,chatUsersVOArrayList,chatListView);
        chatListView.setAdapter(chatLVAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadChatData();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, SplashScreen2.class);
        startActivity(intent);
    }*/
}
