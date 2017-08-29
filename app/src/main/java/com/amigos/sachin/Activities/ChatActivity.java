package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Adapters.ChatArrayAdapter;
import com.amigos.sachin.ChatsFragments.MyChatFragment;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatMessageVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

public class ChatActivity extends AppCompatActivity {

    String myId, userId, userName, imageUrl;
    Context context;
    ValueEventListener valueEventListener;
    Firebase userChatRef, myChatRef;
    ImageView sendIcon;
    private ListView listView;
    ChatArrayAdapter chatArrayAdapter;
    TextView chatText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Firebase.setAndroidContext(this);

        context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_chat);

        final ImageView photoIcon = (ImageView)findViewById(R.id.actionBarPhotoIcon);
        final TextView tv_UserName = (TextView)findViewById(R.id.actionBarUserName);

        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        imageUrl = intent.getStringExtra("imageUrl");
        tv_UserName.setText(userName);
        if(imageUrl != null && !imageUrl.isEmpty()){
            Glide.with(context).load(imageUrl)
                    .bitmapTransform(new CropSquareTransformation(context), new CropCircleTransformation(context))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(photoIcon);
        }
        onInitializeFunctionality();
    }

    public void onInitializeFunctionality(){
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        chatUsersDAO.changeSeen(userId,0);

        chatText = (EditText) findViewById(R.id.msg);

        sendIcon = (ImageView) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String msg = chatText.getText().toString();
                    msg = msg.trim();
                    if(msg == null || msg.isEmpty()){
                        return true;
                    }
                    Firebase myMsgRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/");
                    Firebase newMyMessageRef = myMsgRef.push();
                    Firebase userMsgRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/");
                    Firebase newUserMessageRef = userMsgRef.push();
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    Date date = new Date();
                    String time = dateFormat.format(date);
                    Map<String,Object> message = new HashMap<String,Object>();
                    message.put(myId,msg);
                    message.put("-time",time);
                    newMyMessageRef.updateChildren(message);
                    newUserMessageRef.updateChildren(message);
                    chatText.setText("");

                    ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
                    chatUsersDAO.addToChatList(userId,myId,msg,0);

                    myMsgRef.child(myId).setValue(msg);
                    userMsgRef.child(myId).setValue(msg);

                    return true;
                }
                return false;
            }
        });
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String msg = chatText.getText().toString();
                msg = msg.trim();
                if(msg == null || msg.isEmpty()){
                    return;
                }
                Firebase myMsgRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/");
                Firebase newMyMessageRef = myMsgRef.push();
                Firebase userMsgRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/");
                Firebase newUserMessageRef = userMsgRef.push();
                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                Date date = new Date();
                String time = dateFormat.format(date);
                Map<String,Object> message = new HashMap<String,Object>();
                message.put(myId,msg);
                message.put("-time",time);
                newMyMessageRef.updateChildren(message);
                newUserMessageRef.updateChildren(message);
                chatText.setText("");

                ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
                chatUsersDAO.addToChatList(userId,myId,msg,0);

                myMsgRef.child(myId).setValue(msg);
                userMsgRef.child(myId).setValue(msg);
            }
        });


        chatArrayAdapter = new ChatArrayAdapter(context, R.layout.chat_right);
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        myChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/" );
        userChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/" );

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatArrayAdapter chatArrayAdapter1 = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_right);
                for (DataSnapshot cursorShot : dataSnapshot.getChildren()) {

                    ArrayList<ChatMessageVO> existMsgs= new ArrayList<ChatMessageVO>();
                    String key = "";
                    String receiverMsg = "";
                    String time = "Not available";
                    for(DataSnapshot data: cursorShot.getChildren()){
                        key = "";
                        receiverMsg = "";

                        if (myId.equalsIgnoreCase(data.getKey().toString()) || userId.equalsIgnoreCase(data.getKey().toString())) {
                            key = data.getKey().toString();
                            receiverMsg = data.getValue(String.class);
//
                        }
                        if(data.getKey().equalsIgnoreCase("-time")){
                            time = data.getValue(String.class);
                        }
                        if (key.equalsIgnoreCase(userId)) {
                            chatArrayAdapter1.add(new ChatMessageVO(false, receiverMsg, time));
                        } else if (key.equalsIgnoreCase(myId)) {
                            chatArrayAdapter1.add(new ChatMessageVO(true, receiverMsg, time));
                        }
                    }
                }
                listView.setAdapter(chatArrayAdapter1);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        myChatRef.addValueEventListener(valueEventListener);
    }
    @Override
    public void onBackPressed() {

        myChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/" );
        userChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/" );
        myChatRef.child(myId).setValue("");
        myChatRef.child(userId).setValue("");
        userChatRef.child(myId).setValue("");
        userChatRef.child(userId).setValue("");

        myChatRef.removeEventListener(valueEventListener);

        MyChatFragment.reloadChatList();

        finish();
        return;
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        myChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/" );
        userChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/" );
        myChatRef.child(myId).setValue("");
        myChatRef.child(userId).setValue("");
        userChatRef.child(myId).setValue("");
        userChatRef.child(userId).setValue("");

        myChatRef.removeEventListener(valueEventListener);


    }
}
