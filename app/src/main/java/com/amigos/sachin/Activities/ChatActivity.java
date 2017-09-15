package com.amigos.sachin.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
//import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.Adapters.ChatArrayAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.MyProfileFragments.MyMoods;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatMessageVO;
import com.amigos.sachin.VO.LikedUserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    String myId, userId, userName, imageUrl;
    Context context;
    ValueEventListener valueEventListener;
    Firebase userChatRef, myChatRef;
    ImageView sendIcon;
    private ListView listView;
    ChatArrayAdapter chatArrayAdapter;
    TextView chatText;
    String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Firebase.setAndroidContext(this);

        context = getApplicationContext();
        myName = ApplicationCache.myUserVO.getName();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_chat);

        final ImageView photoIcon = (ImageView)findViewById(R.id.actionBarPhotoIcon);
        final TextView tv_UserName = (TextView)findViewById(R.id.actionBarUserName);
        final ImageView overflowIcon = (ImageView) findViewById(R.id.options_menu);

        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        Firebase myChatRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+myId+"/");
        myChatRef.setValue(null);

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
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        chatUsersDAO.changeSeen(userId,0);

        photoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context,UserProfileActivity.class);
                intent1.putExtra("userId",userId);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });
        tv_UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context,UserProfileActivity.class);
                intent1.putExtra("userId",userId);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });

        overflowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ChatActivity.this,overflowIcon);
                popup.getMenuInflater().inflate(R.menu.chat_overflow_menu, popup.getMenu());
                //Toast.makeText(ChatActivity.this,"You Clicked : ",Toast.LENGTH_SHORT).show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.block:

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(ChatActivity.this,AlertDialog.THEME_HOLO_DARK);
                                builder2.setTitle("Are you sure you want to block "+userName+"!!!");
                                builder2.setMessage("You can find "+userName+" inside Block Users List.");
                                builder2.setCancelable(true);

                                builder2.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                                                Firebase myBlockListRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/block_list"
                                                        +"/people_i_blocked/");
                                                Firebase userBlockListRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/block_list"
                                                        +"/people_who_blocked_me/");
                                                myBlockListRef.child(userId).setValue(time);
                                                userBlockListRef.child(myId).setValue(time);
                                                Toast.makeText(ChatActivity.this,"You have blocked "+userName, Toast.LENGTH_LONG).show();
                                                LikedUserVO likedUserVO = new LikedUserVO ();
                                                likedUserVO.setId(userId);
                                                likedUserVO.setName(userName);
                                                ApplicationCache.peopleIBlockedVOList.add(likedUserVO);
                                                MyMoods.reloadData();
                                                onBackPressed();
                                            }
                                        });

                                builder2.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert12 = builder2.create();
                                alert12.show();
                                return true;
                            case R.id.deleteChat:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this,AlertDialog.THEME_HOLO_DARK);
                                builder1.setTitle("Do you want to delete this chat!!!");
                                builder1.setMessage("You may no longer be able to retreive the chat");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Firebase userChatRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"
                                                        +userId+"/");
                                                userChatRef.setValue(null);
                                                ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
                                                chatUsersDAO.removeFromChatList(userId);
                                                dialog.cancel();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

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

                    if(myName == null){
                        Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/name/");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                myName = dataSnapshot.getValue().toString();
                                ApplicationCache.myUserVO.setName(myName);
                                if (sendMessage()) return;
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }else {
                        if (sendMessage()) return true;

                        return true;
                    }
                }
                return false;
            }
        });
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*if (sendMessage()) return;*/
                if(myName == null){
                    Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/name/");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myName = dataSnapshot.getValue().toString();
                            ApplicationCache.myUserVO.setName(myName);
                            if(sendMessage()) return;
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }else {
                    if (sendMessage()) return;

                    return;
                }
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

        /*valueEventListener = new ValueEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {

            }
        };*/

    }

    private boolean sendMessage() {
        String msg = chatText.getText().toString();
        msg = msg.trim();
        if(msg == null || msg.isEmpty()){
            return true;
        }
        Firebase myMsgRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/");
        Firebase newMyMessageRef = myMsgRef.push();
        Firebase userMsgRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/");
        Firebase newUserMessageRef = userMsgRef.push();
        Firebase messageNotificationRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+userId+"/"+
                myId+"/"+myName+"/" );
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date date = new Date();
        String time = dateFormat.format(date);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        messageNotificationRef.child(timeStamp).setValue(msg);
        Map<String,Object> message = new HashMap<String,Object>();
        message.put(myId,msg);
        message.put("-time",time);
        newMyMessageRef.updateChildren(message);
        newUserMessageRef.updateChildren(message);
        chatText.setText("");

        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(getApplicationContext());
        chatUsersDAO.addToChatList(userId,myId,msg,0);
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        myChatRef.addValueEventListener(valueEventListener);
    }
    @Override
    public void onBackPressed() {

        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        myChatRef = userRef.child("users").child(myId).child("chats").child(userId);
        userChatRef = userRef.child("users").child(userId).child("chats").child(myId);*/
        myChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/" );
        userChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/" );
        myChatRef.removeEventListener(valueEventListener);
        finish();
        return;
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        myChatRef = userRef.child("users").child(myId).child("chats").child(userId);
        userChatRef = userRef.child("users").child(userId).child("chats").child(myId);*/
        myChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/" );
        userChatRef= new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/" );
        myChatRef.removeEventListener(valueEventListener);
    }
}
