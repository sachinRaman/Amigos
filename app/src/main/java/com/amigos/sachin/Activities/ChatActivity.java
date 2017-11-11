package com.amigos.sachin.Activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
//import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.Adapters.ChatArrayAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.DAO.ChatsDAO;
import com.amigos.sachin.MyProfileFragments.MyMoods;
import com.amigos.sachin.R;
import com.amigos.sachin.Services.ChatService;
import com.amigos.sachin.VO.ChatMessageVO;
import com.amigos.sachin.VO.ChatUsersVO;
import com.amigos.sachin.VO.LikedUserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.ChildEventListener;
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

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.ValueEventListener;

public class ChatActivity extends AppCompatActivity  {

    String myId, userId, userName, imageUrl, myName;
    Context context;
    ValueEventListener tempChatValueEventListener, chatStatusValueEventListener;
    ImageView sendIcon ,emojiIcon , photoIcon, overflowIcon;
    private ListView chatListView;
    EmojiconEditText chatText;
    TextView tv_UserName;
    Button chatRequestAcceptButton, chatRequestBlockButton;
    LinearLayout chatRequestLayout;
    boolean chatRequestApproved = false;
    ChatsDAO chatsDAO;
    ChatUsersDAO chatUsersDAO;
    SharedPreferences sp;
    Firebase myAllChatsRef, userAllChatsRef, myTempChatsRef, userTempChatsRef, myChatStatusRef, userChatStatusRef;
    boolean messageSentFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Firebase.setAndroidContext(this);

        context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_chat);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        imageUrl = intent.getStringExtra("imageUrl");



        initializeVariables();

        populateActionBar();

        setUpValueEventListeners();

        setUpOnClickListeners();

        ChatService.thisUserId = userId;

        final Firebase notifyChatRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+myId+"/"+userId+"/");
        notifyChatRef.setValue(null);

        //chatUsersDAO.changeSeen(userId,0);
        updateChatList();

    }

    private void setUpOnClickListeners() {
        chatRequestAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myChatStatusRef.child("chat_request").setValue("1");
                chatRequestLayout.setVisibility(View.GONE);
                chatRequestApproved = true;
            }
        });

        chatRequestBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });


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
                                                chatsDAO.deleteChat(userId);
                                                updateChatList();
                                                /*Firebase userChatRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"
                                                        +userId+"/");
                                                userChatRef.setValue(null);*/

                                                //chatUsersDAO.removeFromChatList(userId);
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

        EmojIconActions emojIconActions = new EmojIconActions(context,(View)findViewById(R.id.chatView), chatText, emojiIcon);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.keyboard_icon, R.drawable.smile_icon);
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

        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()) {
                    userChatStatusRef.child("typing").setValue("1");
                }else{
                    userChatStatusRef.child("typing").setValue("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUpValueEventListeners() {

        tempChatValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String messageId = snapshot.getKey();
                    String message = "";
                    String time = "";
                    for(DataSnapshot chatData: snapshot.getChildren()){
                        if("message".equalsIgnoreCase(chatData.getKey())){
                            message = chatData.getValue().toString();
                        }
                        if("time".equalsIgnoreCase(chatData.getKey())){
                            time = chatData.getValue().toString();
                        }
                    }
                    chatsDAO.addToChatList(userId,messageId,message, time, 1, 1);
                }
                if(dataSnapshot.getValue() != null) {
                    messageSentFlag = true;
                    updateChatList();
                }
                myTempChatsRef.setValue(null);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        chatStatusValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot == null){
                    chatRequestLayout.setVisibility(View.GONE);
                }
                if(!dataSnapshot.hasChild("chat_request")){
                    chatRequestLayout.setVisibility(View.GONE);
                }

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if("typing".equalsIgnoreCase(snapshot.getKey())){
                        if ("0".equalsIgnoreCase(snapshot.getValue().toString())) {
                            tv_UserName.setText(userName);
                        }
                        if ("1".equalsIgnoreCase(snapshot.getValue().toString())) {
                            tv_UserName.setText("...typing...");
                        }
                    }

                    if("reached_message_id".equalsIgnoreCase(snapshot.getKey())){
                        String reachedMessageId = snapshot.getValue().toString();
                        chatsDAO.markMessageAsReached(userId, reachedMessageId);
                        updateChatList();
                    }

                    if("seen_message_id".equalsIgnoreCase(snapshot.getKey())){
                        String seenMessageId = snapshot.getValue().toString();
                        chatsDAO.markMessageAsSeen(userId,seenMessageId);
                        updateChatList();
                    }

                    if("chat_request".equalsIgnoreCase(snapshot.getKey())){
                        if("1".equalsIgnoreCase(snapshot.getValue().toString())){
                            chatRequestLayout.setVisibility(View.GONE);
                            chatRequestApproved = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

    }

    private void populateActionBar() {

        tv_UserName.setText(userName);

        if(imageUrl != null && !imageUrl.isEmpty()){
            Glide.with(context).load(imageUrl)
                    .bitmapTransform(new CropSquareTransformation(context), new CropCircleTransformation(context))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(photoIcon);
        }

        if(userName == null || userName.isEmpty() || imageUrl == null || imageUrl.isEmpty()){
            Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if("name".equalsIgnoreCase(dataSnapshot1.getKey())){
                            userName = dataSnapshot1.getValue().toString();
                            tv_UserName.setText(userName);
                        }
                        if ("imageUrl".equalsIgnoreCase(dataSnapshot1.getKey())){
                            for(DataSnapshot children : dataSnapshot1.getChildren()){
                                if(userId.equalsIgnoreCase(children.getKey())){
                                    imageUrl = children.getValue().toString();
                                    Glide.with(context).load(imageUrl)
                                            .bitmapTransform(new CropSquareTransformation(context), new CropCircleTransformation(context))
                                            .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(photoIcon);
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

    private void initializeVariables() {
        sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        myName = sp.getString("name", "");

        photoIcon = (ImageView)findViewById(R.id.actionBarPhotoIcon);
        tv_UserName = (TextView)findViewById(R.id.actionBarUserName);
        overflowIcon = (ImageView) findViewById(R.id.options_menu);
        sendIcon = (ImageView) findViewById(R.id.send);
        chatListView = (ListView) findViewById(R.id.msgview);
        chatRequestLayout = (LinearLayout) findViewById(R.id.chatRequestLayout);
        chatRequestAcceptButton = (Button) findViewById(R.id.chatRequestAcceptButton);
        chatRequestBlockButton = (Button) findViewById(R.id.chatRequestBlockButton);
        chatText = (EmojiconEditText) findViewById(R.id.msg);
        emojiIcon = (ImageView) findViewById(R.id.smile_icon);

        chatsDAO = new ChatsDAO(context);
        chatsDAO.removeDuplicateEntries(userId); //may make the chat slow
        chatUsersDAO = new ChatUsersDAO(context);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatText.setEmojiconSize(75);

        myAllChatsRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+myId+"/"+userId+ "/my_all_chats/");
        userAllChatsRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+userId+"/"+myId+ "/my_all_chats/");
        myTempChatsRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+myId+"/"+userId+ "/temp_chats/");
        userTempChatsRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+userId+"/"+myId+ "/temp_chats/");
        myChatStatusRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+myId+"/"+userId+ "/chat_status/");
        userChatStatusRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+userId+"/"+myId+ "/chat_status/");
    }

    public void updateChatList(){
        final ChatArrayAdapter chatArrayAdapter1 = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_right);
        final ChatsDAO chatsDAO = new ChatsDAO(context);
        ArrayList<ChatUsersVO> chatUsersVOArrayList = chatsDAO.getMyChatList(userId);

        ArrayList<ChatMessageVO> chatMessageVOArrayList = new ArrayList<ChatMessageVO>();
        String message = "";
        String time = "";
        String seen = "1";
        String messageId = "";

        for(ChatUsersVO chatUsersVO : chatUsersVOArrayList){
            message = chatUsersVO.getLastMessage();
            time = chatUsersVO.getTime();
            seen = String.valueOf(chatUsersVO.getSeen());
            messageId = chatUsersVO.getMessageId();
            if(chatUsersVO.getUserSide() == 0){
                chatArrayAdapter1.add(new ChatMessageVO(true, message, time, seen,userId));
            }else{
                chatArrayAdapter1.add(new ChatMessageVO(false, message, time, seen,userId));
            }
        }

        chatUsersDAO.changeSeen(userId,0);

        chatListView.setAdapter(chatArrayAdapter1);
        userChatStatusRef.child("seen_message_id").setValue(messageId);

        if(messageSentFlag) {
            chatUsersDAO.addToChatList(userId, myId, message, 0);
        }
        messageSentFlag = false;
    }

    private boolean sendMessage() {


        String msg = chatText.getText().toString();
        msg = msg.trim();


        if(msg == null || msg.isEmpty()){
            return true;
        }
        if (chatRequestApproved == false){
            chatRequestApproved = true;
            chatRequestLayout.setVisibility(View.GONE);
            myChatStatusRef.child("chat_request").setValue("1");
        }

        //sendNotification
        Firebase messageNotificationRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+userId+"/"+
                myId+"/"+myName+"/" );
        DateFormat dateFormat = new SimpleDateFormat("dd MMM hh:mm a");
        Date date = new Date();
        String time = dateFormat.format(date);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        messageNotificationRef.child(timeStamp).setValue(msg);

        Map<String,Object> message = new HashMap<String,Object>();
        message.put("message",msg);
        message.put("time",time);


        myAllChatsRef.child(timeStamp).updateChildren(message);
        userAllChatsRef.child(timeStamp).updateChildren(message);
        userTempChatsRef.child(timeStamp).updateChildren(message);

        chatsDAO.addToChatList(userId,timeStamp,msg, time, 1, 0);
        messageSentFlag = true;
        updateChatList();
        chatText.setText("");

        chatUsersDAO.addToChatList(userId,myId,msg,0);
        return false;
    }


    @Override
    public void onResume(){
        super.onResume();
        myTempChatsRef.addValueEventListener(tempChatValueEventListener);
        myChatStatusRef.addValueEventListener(chatStatusValueEventListener);
        ChatService.thisUserId = userId;
    }
    @Override
    public void onBackPressed() {
        userChatStatusRef.child("typing").setValue("0");
        ChatService.thisUserId = null;
        myTempChatsRef.removeEventListener(tempChatValueEventListener);
        myChatStatusRef.removeEventListener(chatStatusValueEventListener);
        finish();
        return;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        userChatStatusRef.child("typing").setValue("0");
        ChatService.thisUserId = null;
        myTempChatsRef.removeEventListener(tempChatValueEventListener);
        myChatStatusRef.removeEventListener(chatStatusValueEventListener);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
