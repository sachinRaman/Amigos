package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;




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
    String myId, userFcmToken;
    String age = null, sex = null, place = null;
    String userName , userId , imageUrl, myName ;
    TextView tv_interests1, tv_interests2, tv_interests3;
    TagView tags_interests1, tags_interests2, tags_interests3;

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

        /*tv_interests1.setVisibility(View.GONE);
        tv_interests2.setVisibility(View.GONE);
        tv_interests3.setVisibility(View.GONE);*/
        tv_act1.setVisibility(View.GONE);
        tv_act2.setVisibility(View.GONE);
        tv_professionalProfileText.setVisibility(View.GONE);
        tv_InterestedInText.setVisibility(View.GONE);

        /*tags_interests1.setVisibility(View.GONE);
        tags_interests2.setVisibility(View.GONE);
        tags_interests3.setVisibility(View.GONE);*/

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
                                    tv_InterestedInText.setVisibility(View.VISIBLE);
                                    tv_act1.setVisibility(View.VISIBLE);
                                    tv_act1.setText(children.getValue(String.class));
                                } else {
                                    tv_act1.setVisibility(View.GONE);
                                    tv_InterestedInText.setVisibility(View.GONE);
                                }
                            }
                            if ("act2".equalsIgnoreCase(children.getKey())) {
                                if (children.getValue(String.class) != null && !children.getValue(String.class).trim().isEmpty()) {
                                    tv_professionalProfileText.setVisibility(View.VISIBLE);
                                    tv_act2.setVisibility(View.VISIBLE);
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
                    if("fcmToken".equalsIgnoreCase(userData.getKey())){
                        userFcmToken = userData.getValue().toString();
                    }
                    if("interests".equalsIgnoreCase(userData.getKey())){
                        ArrayList<String> interests1 = new ArrayList<String>();
                        ArrayList<String> interests2 = new ArrayList<String>();
                        ArrayList<String> interests3 = new ArrayList<String>();

                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();

                            if("interests1".equalsIgnoreCase(key)){
                                interests1.clear();
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests1.add(snap1.getKey());
                                }
                                updateTags(tv_interests1,tags_interests1,interests1);
                            }
                            if("interests2".equalsIgnoreCase(key)){
                                interests2.clear();
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests2.add(snap1.getKey());
                                }
                                updateTags(tv_interests2,tags_interests2,interests2);
                            }
                            if("interests3".equalsIgnoreCase(key)){
                                interests3.clear();
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests3.add(snap1.getKey());
                                }
                                updateTags(tv_interests3,tags_interests3,interests3);
                            }
                        }

                        ArrayList<ArrayList<String>> userInterests = new ArrayList<ArrayList<String>>(Arrays.asList(interests1,
                                interests2, interests3));
                        ArrayList<ArrayList<String>> myInterests = new ArrayList<ArrayList<String>>(Arrays.asList(interestsList1,
                                interestsList2, interestsList3));
                        match = Algorithms.calculateMatch(myInterests, userInterests);
                        tv_matchCount.setText("Clicks "+match+ "%");
                        if(myId.equalsIgnoreCase(finalUserId)){
                            tv_matchCount.setVisibility(View.GONE);
                            tv_bullet.setVisibility(View.GONE);
                            tv_admired_you.setVisibility(View.GONE);
                        }
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
                if(peopleILikedArrayList.contains(finalUserId)){
                    check_icon.setVisibility(View.GONE);
                    Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/people_i_liked/");
                    Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + finalUserId1 + "/people_who_liked_me/");
                    myRef.child(finalUserId1).setValue(null);
                    userRef.child(myId).setValue(null);
                    peopleILikedDAO.removeFromPeopleILikedList(finalUserId1);
                    Toast.makeText(context,"You have unadmired "+ userName,Toast.LENGTH_SHORT).show();
                }else {
                    check_icon.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "You admired " + userName, Toast.LENGTH_SHORT).show();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/people_i_liked/");
                    Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + finalUserId1 + "/people_who_liked_me/");
                    Firebase likedNotificationRef = new Firebase("https://new-amigos.firebaseio.com/liked_notifications/"
                            + finalUserId1 + "/");

                    Firebase admiredNotificationRef = new Firebase("https://new-amigos.firebaseio.com/admired_notifications/"
                            + finalUserId1 + "/"+myId+"/");
                    myRef.child(finalUserId1).setValue(timeStamp);
                    userRef.child(myId).setValue(timeStamp);
                    likedNotificationRef.child(myId).child(myName).setValue(timeStamp);
                    admiredNotificationRef.child("name").setValue(myName);
                    admiredNotificationRef.child("timeStamp").setValue(timeStamp);
                    admiredNotificationRef.child("fcmToken").setValue(userFcmToken);

                    Firebase thisUserRef = new Firebase("https://new-amigos.firebaseio.com/users/" + finalUserId1 + "/");
                    thisUserRef.addValueEventListener(new ValueEventListener() {
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
                            //PeopleILikedFragment.reloadPeopleILikedList();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });
    }

    private synchronized void updateTags(TextView tv_interests, TagView tags_interests, ArrayList<String> interests) {
        if(interests == null || interests.isEmpty()){
            tv_interests.setVisibility(View.GONE);
            //tags_interests.setVisibility(View.INVISIBLE);
            return;
        }else{
            tv_interests.setVisibility(View.VISIBLE);
            //tags_interests.setVisibility(View.VISIBLE);

            ArrayList<Tag> tags = new ArrayList<Tag>();
            tags.clear();
            for(String s : interests){
                Tag tag = new Tag(s);
                tag.tagTextColor = context.getResources().getColor(R.color.colorPrimary);
                tag.layoutBorderColor = context.getResources().getColor(R.color.colorPrimary);
                tag.layoutColor = Color.parseColor("#FFFFFF");
                tag.layoutBorderSize = 1.0F;
                tag.layoutColorPress = Color.WHITE;
                tags.add(tag);
            }
            tags_interests.addTags(tags);
        }
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
}
