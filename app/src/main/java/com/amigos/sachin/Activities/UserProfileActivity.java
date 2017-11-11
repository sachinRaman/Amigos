package com.amigos.sachin.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.ChatsFragments.PeopleILikedFragment;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.Utils.Algorithms;
import com.amigos.sachin.VO.UserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profilePicture;
    TextView  tv_act3, tv_matchCount, tv_professionalProfileText,
            tv_InterestedInText, tv_moodTopic, tv_bullet, tv_admired_you;
    EmojiconTextView tv_name, tv_extra_info, tv_status, tv_act1, tv_act2;
    ArrayList<String> userInterests = new ArrayList<String>();
    ArrayList<String> myInterests = new ArrayList<String>();
    Context context;
    int match = 0;
    ArrayList<Tag> tags = new ArrayList<Tag>();
    ImageView messageIcon, likeIcon, check_icon;
    String myId;
    String age = null, sex = null, place = null;
    String userName , userId , imageUrl, myName ;
    TextView tv_interests1, tv_interests2, tv_interests3;
    TagView tags_interests1, tags_interests2, tags_interests3;
    /*EditText et_search_all_users1;
    LinearLayout linear_layout_search1;*/

    ArrayList<String> interestsList1 = new ArrayList<String>();
    ArrayList<String> interestsList2 = new ArrayList<String>();
    ArrayList<String> interestsList3 = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = getApplicationContext();
        Firebase.setAndroidContext(context);
        final SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        UserVO thisUserVO;

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        UserVO myVO = ApplicationCache.myUserVO;
        /*myInterests = myVO.getInterests();*/
        myName = sp.getString("name","");

        Set<String> interests = sp.getStringSet("interests", new HashSet<String>());
        myInterests.addAll(interests);

        final ArrayList<String> sentChatRequests = new ArrayList<String>();
        final ArrayList<String> pendingChatRequests = new ArrayList<String>();
        final ArrayList<String> approvedChatRequests = new ArrayList<String>();

        final Set<String> sentRequestsSet = sp.getStringSet("sent_requests", new HashSet<String>());
        sentChatRequests.addAll(sentRequestsSet);

        final Set<String> pendingRequestsSet = sp.getStringSet("pending_requests", new HashSet<String>());
        pendingChatRequests.addAll(pendingRequestsSet);

        final Set<String> approvedRequestsSet = sp.getStringSet("approved_requests", new HashSet<String>());
        approvedChatRequests.addAll(approvedRequestsSet);


        tv_name = (EmojiconTextView) findViewById(R.id.tv_display_name1);
        tv_extra_info = (EmojiconTextView) findViewById(R.id.tv_extra_info1) ;
        tv_status = (EmojiconTextView) findViewById(R.id.textView_info1);
        tv_act1 = (EmojiconTextView) findViewById(R.id.tv_activity11);
        tv_act2 = (EmojiconTextView) findViewById(R.id.tv_activity21);
        profilePicture = (ImageView) findViewById(R.id.imageView_profileImage1);
        messageIcon = (ImageView) findViewById(R.id.messageIcon1);
        likeIcon = (ImageView) findViewById(R.id.likeIcon);
        check_icon = (ImageView) findViewById(R.id.check_icon);
        tv_matchCount = (TextView) findViewById(R.id.tv_click1);
        tv_professionalProfileText = (TextView) findViewById(R.id.tv_professionalProfile);
        tv_InterestedInText = (TextView) findViewById(R.id.tv_interestedActivities);
        tv_admired_you = (TextView) findViewById(R.id.tv_admired_you1);
        tv_bullet = (TextView) findViewById(R.id.tv_bullet1);

        tv_interests1 = (TextView) findViewById(R.id.tv_interests11);
        tv_interests2 = (TextView) findViewById(R.id.tv_interests21);
        tv_interests3 = (TextView) findViewById(R.id.tv_interests31);

        tags_interests1 = (TagView) findViewById(R.id.tags_interests11);
        tags_interests2 = (TagView) findViewById(R.id.tags_interests21);
        tags_interests3 = (TagView) findViewById(R.id.tags_interests31);

        tv_name.setEmojiconSize(40);
        tv_extra_info.setEmojiconSize(40);
        tv_status.setEmojiconSize(40);
        tv_act1.setEmojiconSize(40);
        tv_act2.setEmojiconSize(40);

        tv_interests1.setVisibility(View.GONE);
        tv_interests2.setVisibility(View.GONE);
        tv_interests3.setVisibility(View.GONE);

        tags_interests1.setVisibility(View.GONE);
        tags_interests2.setVisibility(View.GONE);
        tags_interests3.setVisibility(View.GONE);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        tv_matchCount.setText("");

        final String finalUserId = userId;

        final Set<String> interests1 = sp.getStringSet("interests1", new HashSet<String>());
        interestsList1.addAll(interests1);

        final Set<String> interests2 = sp.getStringSet("interests2", new HashSet<String>());
        interestsList2.addAll(interests2);

        final Set<String> interests3 = sp.getStringSet("interests3", new HashSet<String>());
        interestsList3.addAll(interests3);

        Firebase myAdmirereRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/people_who_liked_me/");
        myAdmirereRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> myAdmirers = new ArrayList<String>();
                myAdmirers.clear();
                if(dataSnapshot != null){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        myAdmirers.add(dataSnapshot1.getKey());
                    }
                }
                if(myAdmirers.contains(finalUserId)){
                    tv_bullet.setVisibility(View.VISIBLE);
                    tv_admired_you.setVisibility(View.VISIBLE);
                }else{
                    tv_bullet.setVisibility(View.GONE);
                    tv_admired_you.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + userId + "/");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    if ("name".equalsIgnoreCase(userData.getKey())) {
                        tv_name.setText(userData.getValue(String.class));
                        userName = userData.getValue(String.class);
                        getSupportActionBar().setTitle(userName);
                    }
                    if ("age".equalsIgnoreCase(userData.getKey())) {
                        age = userData.getValue(String.class);
                    }
                    if ("place".equalsIgnoreCase(userData.getKey())) {
                        place = userData.getValue(String.class);
                    }
                    if ("sex".equalsIgnoreCase(userData.getKey())) {
                        sex = userData.getValue(String.class);
                    }
                    if ("status".equalsIgnoreCase(userData.getKey())) {
                        if (userData.getValue(String.class) != null && !userData.getValue(String.class).isEmpty()) {
                            tv_status.setText(userData.getValue(String.class));
                        } else {
                            tv_status.setVisibility(View.GONE);
                        }
                    }
                    if ("activity".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if ("act1".equalsIgnoreCase(children.getKey())) {
                                if (children.getValue(String.class) != null && !children.getValue(String.class).trim().isEmpty()) {
                                    tv_act1.setText(children.getValue(String.class));
                                } else {
                                    tv_act1.setVisibility(View.GONE);
                                    tv_InterestedInText.setVisibility(View.GONE);
                                }
                            }
                            if ("act2".equalsIgnoreCase(children.getKey())) {
                                if (children.getValue(String.class) != null && !children.getValue(String.class).trim().isEmpty()) {
                                    tv_act2.setText(children.getValue(String.class));
                                } else {
                                    tv_professionalProfileText.setVisibility(View.GONE);
                                    tv_act2.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    if ("moods".equalsIgnoreCase(userData.getKey())) {
                        String mood = "";
                        String moodTopic = "";
                        for (DataSnapshot children : userData.getChildren()) {
                            if ("mood".equalsIgnoreCase(children.getKey())) {
                                mood = children.getValue().toString();
                            }
                            if ("topic".equalsIgnoreCase(children.getKey())) {
                                moodTopic = children.getValue().toString();
                            }
                        }

                    }
                    if ("imageUrl".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if (userId.equalsIgnoreCase(children.getKey())) {
                                imageUrl = children.getValue().toString();
                                if (imageUrl != null) {
                                    Glide.with(context).load(imageUrl)
                                            .bitmapTransform(new CropSquareTransformation(context), new CropCircleTransformation(context))
                                            .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);
                                }
                            }
                        }
                    }
                    if("interests".equalsIgnoreCase(userData.getKey())){

                        tv_interests1.setVisibility(View.VISIBLE);
                        tv_interests2.setVisibility(View.VISIBLE);
                        tv_interests3.setVisibility(View.VISIBLE);

                        tags_interests1.setVisibility(View.VISIBLE);
                        tags_interests2.setVisibility(View.VISIBLE);
                        tags_interests3.setVisibility(View.VISIBLE);

                        ArrayList<String> interests1 = new ArrayList<String>();
                        ArrayList<String> interests2 = new ArrayList<String>();
                        ArrayList<String> interests3 = new ArrayList<String>();

                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();

                            if("interests1".equalsIgnoreCase(key)){
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests1.add(snap1.getKey());
                                }
                            }
                            if("interests2".equalsIgnoreCase(key)){
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests2.add(snap1.getKey());
                                }
                            }
                            if("interests3".equalsIgnoreCase(key)){
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests3.add(snap1.getKey());
                                }
                            }
                        }
                        updateTags(tv_interests1,tags_interests1,interests1);
                        updateTags(tv_interests2,tags_interests2,interests2);
                        updateTags(tv_interests3,tags_interests3,interests3);

                        ArrayList<ArrayList<String>> userInterests = new ArrayList<ArrayList<String>>(Arrays.asList(interests1,
                                interests2, interests3));
                        ArrayList<ArrayList<String>> myInterests = new ArrayList<ArrayList<String>>(Arrays.asList(interestsList1,
                                interestsList2, interestsList3));
                        match = Algorithms.calculateMatch(myInterests, userInterests);
                        tv_matchCount.setText("Clicks "+match+ "%");
                        if(match == 0){
                            tv_matchCount.setVisibility(View.GONE);
                            tv_bullet.setVisibility(View.GONE);
                        }

                    }
                }
                String finalInfo = "";
                if (age != null && !age.isEmpty() && isNumeric(age.trim())) {
                    finalInfo += age + " / ";
                }
                if (sex != null && !sex.isEmpty()) {
                    finalInfo += sex + " / ";
                }
                if (place != null && !place.isEmpty()) {
                    finalInfo += place;
                } else if (!finalInfo.isEmpty()) {
                    finalInfo = finalInfo.substring(0, finalInfo.length() - 3);
                }
                tv_extra_info.setText(finalInfo);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ZoomedProfileImageActivity.class);

                intent.putExtra("imageUrl",imageUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Get the transition name from the string
                String transitionName = getString(R.string.zoom_transition_string);
                startActivity(intent);

                /*ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this,
                                profilePicture,   // Starting view
                                transitionName    // The String
                        );
                //Start the Intent
                ActivityCompat.startActivity(context, intent, options.toBundle());*/
            }
        });

        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);

                /*final Firebase chatRequestReceivedRef = new Firebase("https://new-amigos.firebaseio.com/chat_requests/"+finalUserId+"/request_received/");
                final Firebase chatRequestApprovedRef = new Firebase("https://new-amigos.firebaseio.com/chat_requests/"+finalUserId+"/request_approved/");

                if(sentChatRequests.contains(finalUserId)){
                    Toast.makeText(context,"You have already sent the chat request", Toast.LENGTH_SHORT).show();
                }else if(pendingChatRequests.contains(finalUserId)){
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(UserProfileActivity.this,AlertDialog.THEME_HOLO_DARK);
                    builder2.setTitle("Request already received");
                    builder2.setMessage(userName + " has already requested to chat with you. Do you wish to continue?");
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Set<String> approvedChats = new HashSet<String>(approvedChatRequests);
                                    Set<String> pendingChats = new HashSet<String>(pendingChatRequests);

                                    approvedChats.add(finalUserId);
                                    approvedChatRequests.add(finalUserId);

                                    pendingChats.remove(finalUserId);
                                    pendingChats.remove(finalUserId);

                                    Firebase myChatRequestRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chat_requests/"+ finalUserId);
                                    Firebase userChatRequestRef = new Firebase("https://new-amigos.firebaseio.com/users/"+finalUserId+"/chat_requests/"+ myId);
                                    myChatRequestRef.setValue("2");
                                    userChatRequestRef.setValue("2");

                                    sp.edit().putStringSet("approved_requests", approvedChats)
                                            .putStringSet("pending_requests", pendingChats)
                                            .apply();

                                    chatRequestApprovedRef.child(myId).setValue(myName);

                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("userName", userName);
                                    intent.putExtra("imageUrl", imageUrl);
                                    startActivity(intent);

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
                }else if(approvedChatRequests.contains(finalUserId)) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userName", userName);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(UserProfileActivity.this,AlertDialog.THEME_HOLO_DARK);
                    builder2.setTitle("Send Chat request");
                    builder2.setMessage("Do you want to send chat request to "+ userName+"?");
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Set<String> sentChats = new HashSet<String>(sentChatRequests);
                                    sentChats.add(finalUserId);
                                    sentChatRequests.add(finalUserId);


                                    Firebase myChatRequestRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chat_requests/"+ finalUserId);
                                    Firebase userChatRequestRef = new Firebase("https://new-amigos.firebaseio.com/users/"+finalUserId+"/chat_requests/"+ myId);
                                    myChatRequestRef.setValue("0");
                                    userChatRequestRef.setValue("1");

                                    sp.edit().putStringSet("sent_requests", sentChats)
                                            .apply();

                                    chatRequestReceivedRef.child(myId).setValue(myName);
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
                }*/

            }
        });

        final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
        ArrayList<String> peopleILikedArrayList = peopleILikedDAO.getAllPeopleIlikedId();
        if(peopleILikedArrayList.contains(finalUserId)){
            check_icon.setVisibility(View.VISIBLE);
        }

        final String finalUserId1 = userId;
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
                ArrayList<String> peopleILikedArrayList = peopleILikedDAO.getAllPeopleIlikedId();
                if(peopleILikedArrayList.contains(userId)){
                    Toast.makeText(context,"You have already admired "+ userName,Toast.LENGTH_SHORT).show();
                }else {
                    check_icon.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "You admired " + userName, Toast.LENGTH_SHORT).show();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/people_i_liked/");
                    Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + finalUserId1 + "/people_who_liked_me/");
                    Firebase likedNotificationRef = new Firebase("https://new-amigos.firebaseio.com/liked_notifications/"
                            + finalUserId1 + "/");
                    myRef.child(finalUserId1).setValue(timeStamp);
                    userRef.child(myId).setValue(timeStamp);
                    likedNotificationRef.child(myId).child(myName).setValue(timeStamp);

                    Firebase userRef1 = new Firebase("https://new-amigos.firebaseio.com/users/" + finalUserId1 + "/");
                    userRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = null, status = null, imageUrl = null;
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                if ("name".equalsIgnoreCase(snap.getKey())) {
                                    name = snap.getValue().toString();
                                }
                                if ("status".equalsIgnoreCase(snap.getKey())) {
                                    status = snap.getValue().toString();
                                }
                                if ("imageUrl".equalsIgnoreCase(snap.getKey())) {
                                    for (DataSnapshot children : snap.getChildren()) {
                                        if (finalUserId1.equalsIgnoreCase(children.getKey())) {
                                            imageUrl = children.getValue().toString();
                                        }
                                    }
                                }
                            }
                            peopleILikedDAO.addUserToPeopleILikedList(finalUserId1, name, status, imageUrl);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });
    }

    private void updateTags(TextView tv_interests1, TagView tags_interests1, ArrayList<String> interests1) {
        if(interests1 == null || interests1.isEmpty()){
            tv_interests1.setVisibility(View.GONE);
            tags_interests1.setVisibility(View.GONE);
        }else{
            ArrayList<Tag> tags1 = new ArrayList<Tag>();
            tags1.clear();
            for(String s : interests1){
                Tag tag = new Tag(s);
                tag.tagTextColor = context.getResources().getColor(R.color.colorPrimary);
                tag.layoutBorderColor = context.getResources().getColor(R.color.colorPrimary);
                tag.layoutColor = Color.parseColor("#FFFFFF");
                tag.layoutBorderSize = 1.0F;
                tag.layoutColorPress = Color.WHITE;
                tags1.add(tag);
            }
            tags_interests1.addTags(tags1);
        }
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
}
