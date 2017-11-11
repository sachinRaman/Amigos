package com.amigos.sachin.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.amigos.sachin.MyProfileFragments.MyMoods;
import com.amigos.sachin.R;
import com.amigos.sachin.Services.ChatService;
import com.amigos.sachin.VO.ChatMessageVO;
import com.amigos.sachin.VO.LikedUserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

public class NewChatActivity extends AppCompatActivity {

    String myId, userId, userName, imageUrl;
    Context context;
    ValueEventListener valueEventListener;
    Firebase userChatRef, myChatRef;
    ImageView sendIcon,emojiIcon;
    private ListView listView;
    ChatArrayAdapter chatArrayAdapter;
    EmojiconEditText chatText;
    String myName;
    Firebase thisChatRef;
    Button chatRequestAcceptButton, chatRequestBlockButton;
    LinearLayout chatRequestLayout;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        Firebase.setAndroidContext(this);

        context = getApplicationContext();
        //myName = ApplicationCache.myUserVO.getName();
        //chatFlag = 1;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_chat);

        //cancelNotification(context,12345);

        final ImageView photoIcon = (ImageView)findViewById(R.id.actionBarPhotoIcon);
        final TextView tv_UserName = (TextView)findViewById(R.id.actionBarUserName);
        final ImageView overflowIcon = (ImageView) findViewById(R.id.options_menu);

        chatRequestLayout = (LinearLayout) findViewById(R.id.chatRequestLayout1);
        chatRequestAcceptButton = (Button) findViewById(R.id.chatRequestAcceptButton1);
        chatRequestBlockButton = (Button) findViewById(R.id.chatRequestBlockButton1);

        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        myName = sp.getString("name", "");


        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        imageUrl = intent.getStringExtra("imageUrl");

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

        tv_UserName.setText(userName);

        ChatService.thisUserId = userId;

        final Firebase myChatRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+myId+"/"+userId+"/");
        thisChatRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chats/"+myId+"/"+myId+"/typing/");
        myChatRef.setValue(null);



        Firebase typingChatRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/"+userId+"/"+userId+"/typing/");

        typingChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    if ("0".equalsIgnoreCase(dataSnapshot.getValue().toString())) {
                        tv_UserName.setText(userName);
                    }
                    if ("1".equalsIgnoreCase(dataSnapshot.getValue().toString())) {
                        tv_UserName.setText("...typing...");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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
                PopupMenu popup = new PopupMenu(NewChatActivity.this,overflowIcon);
                popup.getMenuInflater().inflate(R.menu.chat_overflow_menu, popup.getMenu());
                //Toast.makeText(ChatActivity.this,"You Clicked : ",Toast.LENGTH_SHORT).show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.block:

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(NewChatActivity.this,AlertDialog.THEME_HOLO_DARK);
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
                                                Toast.makeText(NewChatActivity.this,"You have blocked "+userName, Toast.LENGTH_LONG).show();
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(NewChatActivity.this,AlertDialog.THEME_HOLO_DARK);
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

        final Firebase myChatRequestRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chat_requests/"+ userId+"/" );
        final Firebase userChatRequestRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/chat_requests/"+ myId+ "/");

        myChatRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if("status".equalsIgnoreCase(snapshot.getKey())){
                        if("2".equalsIgnoreCase(snapshot.getValue().toString())){
                            chatRequestLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        chatRequestAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myChatRequestRef.child("status").setValue("2");
                chatRequestLayout.setVisibility(View.GONE);
            }
        });

        chatRequestBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(NewChatActivity.this,AlertDialog.THEME_HOLO_DARK);
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
                                Toast.makeText(NewChatActivity.this,"You have blocked "+userName, Toast.LENGTH_LONG).show();
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


        onInitializeFunctionality();

    }

    public void onInitializeFunctionality() {
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        chatUsersDAO.changeSeen(userId,0);

        chatText = (EmojiconEditText) findViewById(R.id.msg1);
        chatText.setEmojiconSize(75);

        sendIcon = (ImageView) findViewById(R.id.send1);
        listView = (ListView) findViewById(R.id.msgview1);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        emojiIcon = (ImageView) findViewById(R.id.smile_icon1);

        EmojIconActions emojIconActions = new EmojIconActions(context,(View)findViewById(R.id.chatView), chatText, emojiIcon);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.keyboard_icon, R.drawable.smile_icon);

        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    //if (sendMessage()) return true;

                    return true;
                }
                return false;
            }
        });
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //if (sendMessage()) return;

                return;
            }
        });

        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()) {
                    thisChatRef.setValue("1");
                }else{
                    thisChatRef.setValue("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    String time = "";
                    String seen = "1";

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
                        if(data.getKey().equalsIgnoreCase("-seen")){
                            seen = data.getValue(String.class);
                        }
                        if (key.equalsIgnoreCase(userId)) {
                            chatArrayAdapter1.add(new ChatMessageVO(false, receiverMsg, time, seen,userId));
                        } else if (key.equalsIgnoreCase(myId)) {
                            chatArrayAdapter1.add(new ChatMessageVO(true, receiverMsg, time, seen, userId));
                        }
                    }
                }
                listView.setAdapter(chatArrayAdapter1);
                //markMessageAsSeen();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }
}
