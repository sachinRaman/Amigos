package com.amigos.sachin.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

        chatListView = (ListView) findViewById(R.id.chatListView1);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        tv_emptyChat = (TextView) findViewById(R.id.tv_emptyChat1);

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("reloadChatList"));

        Firebase activeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/active/");
        activeRef.setValue("1");

        loadChatData();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            loadChatData();
        }
    };

    private static void loadChatData() {
        if(context == null || myId == null){
            return;
        }
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        ArrayList<ChatUsersVO> chatUsersVOArrayList = chatUsersDAO.getMyChatList(myId);
        SharedPreferences sp;
        sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        Set<String> blockedUsersSet = sp.getStringSet("blocked", new HashSet<String>());
        ArrayList<String> blockedUsers = new ArrayList<String>();
        blockedUsers.addAll(blockedUsersSet);

        for( int i = chatUsersVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = chatUsersVOArrayList.get(i).getUserId();
            if( blockedUsers.contains(userId)){
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

    @Override
    protected void onResume() {
        Firebase activeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/active/");
        activeRef.setValue("1");
        super.onResume();
        loadChatData();
    }
    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, SplashScreen2.class);
        startActivity(intent);
    }*/
}
