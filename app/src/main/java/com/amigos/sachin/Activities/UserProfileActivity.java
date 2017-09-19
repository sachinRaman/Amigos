package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.ChatsFragments.PeopleILikedFragment;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.R;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.glide.transformations.CropSquareTransformation;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profilePicture;
    TextView tv_name, tv_extra_info, tv_status, tv_act1, tv_act2, tv_act3, tv_matchCount, tv_professionalProfileText,
            tv_InterestedInText, tv_moodTopic;
    ArrayList<String> userInterests = new ArrayList<String>();
    ArrayList<String> myInterests = new ArrayList<String>();
    Context context;
    int match = 0;
    TagView tagGroup;
    ArrayList<Tag> tags = new ArrayList<Tag>();
    ImageView messageIcon, likeIcon;
    String myId;
    String age = null, sex = null, place = null;
    String userName , userId , imageUrl, myName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = getApplicationContext();
        Firebase.setAndroidContext(context);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        UserVO thisUserVO;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        UserVO myVO = ApplicationCache.myUserVO;
        /*myInterests = myVO.getInterests();*/
        myName = sp.getString("name","");

        Set<String> interests = sp.getStringSet("interests", new HashSet<String>());
        myInterests.addAll(interests);

        tv_name = (TextView) findViewById(R.id.tv_display_name1);
        tv_extra_info = (TextView) findViewById(R.id.tv_extra_info1) ;
        tv_status = (TextView) findViewById(R.id.textView_info1);
        tv_act1 = (TextView) findViewById(R.id.tv_activity11);
        tv_act2 = (TextView) findViewById(R.id.tv_activity21);
        /*tv_act3 = (TextView) findViewById(R.id.tv_activity31);*/
        profilePicture = (ImageView) findViewById(R.id.imageView_profileImage1);
        tagGroup = (TagView) findViewById(R.id.user_tag_group1);
        messageIcon = (ImageView) findViewById(R.id.messageIcon1);
        likeIcon = (ImageView) findViewById(R.id.likeIcon);
        tv_matchCount = (TextView) findViewById(R.id.tv_matchCount1);
        tv_professionalProfileText = (TextView) findViewById(R.id.tv_professionalProfile);
        tv_InterestedInText = (TextView) findViewById(R.id.tv_interestedActivities);
        tv_moodTopic = (TextView)findViewById(R.id.tv_moodTopic);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        tv_matchCount.setText("YOU CLICK 0%");

        /*thisUserVO = ApplicationCache.thisUserVOMap.get(userId);*/
        /*if(thisUserVO != null){
            tv_name.setText(thisUserVO.getName());
            userName = thisUserVO.getName();

            age = thisUserVO.getAge();

            place = thisUserVO.getPlace();

            if(thisUserVO.getStatus() != null && !thisUserVO.getStatus().isEmpty()){
                tv_status.setText(thisUserVO.getStatus());
            }else{
                tv_status.setVisibility(View.GONE);
            }

            if(thisUserVO.getActivity1() != null && !thisUserVO.getActivity1().trim().isEmpty()){
                tv_act1.setText(thisUserVO.getActivity1());
            }else{
                tv_act1.setVisibility(View.GONE);
                tv_InterestedInText.setVisibility(View.GONE);
            }

            if(thisUserVO.getActivity2() != null && !thisUserVO.getActivity2().trim().isEmpty()){
                tv_act2.setText(thisUserVO.getActivity2());
            }else{
                tv_act2.setVisibility(View.GONE);
                tv_InterestedInText.setVisibility(View.GONE);
            }

            if("1".equalsIgnoreCase(thisUserVO.getMood()) && thisUserVO.getMoodTopic() != null && !thisUserVO.getMoodTopic().trim().isEmpty()){
                tv_moodTopic.setText(thisUserVO.getMoodTopic());
            }else{
                tv_moodTopic.setVisibility(View.GONE);
            }

            imageUrl = thisUserVO.getImageUrl();
            if (imageUrl != null){
                Glide.with(context).load(imageUrl)
                        .bitmapTransform(new CropSquareTransformation(context))
                        .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);
            }

            userInterests = thisUserVO.getInterests();

            ArrayList<String> commonInterests = new ArrayList<String>();
            if(myInterests != null){
                int matchCount = 0;
                commonInterests.clear();
                for(String s : userInterests){
                    if(myInterests.contains(s)){
                        matchCount++;
                        commonInterests.add(s);
                    }
                }
                match = Math.round(((float)matchCount/(myInterests.size()))*100);
                if (match == 0){
                    tv_matchCount.setText("YOU CLICK 0%");
                }else if(match%100 == 0){
                    tv_matchCount.setText("YOU CLICK 100%");
                }else{
                    tv_matchCount.setText("YOU CLICK "+match%100+"%");
                }
            }
            tags.clear();
            for(String s : commonInterests){
                Tag tag = new Tag(s);
                tag.tagTextColor = Color.parseColor("#F4514E");
                tag.layoutBorderColor = Color.parseColor("#F4514E");
                tag.layoutColor = Color.parseColor("#FFFFFF");
                tag.layoutBorderSize = 1.0F;
                tag.layoutColorPress = Color.WHITE;
                tags.add(tag);
                match++;
            }
            tagGroup.addTags(tags);

            String finalInfo = "";
            if( age != null && !age.isEmpty()){
                finalInfo += age+" / ";
            }
            if(sex != null &&  !sex.isEmpty()){
                finalInfo += sex+" / ";
            }
            if(place != null && !place.isEmpty()){
                finalInfo += place;
            }else if(!finalInfo.isEmpty()){
                finalInfo = finalInfo.substring(0, finalInfo.length() - 3);
            }
            tv_extra_info.setText(finalInfo);
        }else {
            Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + userId + "/");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userData : dataSnapshot.getChildren()) {
                        if ("name".equalsIgnoreCase(userData.getKey())) {
                            tv_name.setText(userData.getValue(String.class));
                            userName = userData.getValue(String.class);
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
                            *//*if("act3".equalsIgnoreCase(children.getKey())){
                                if(children.getValue(String.class) != null && !children.getValue(String.class).isEmpty()){
                                    tv_act3.setText(children.getValue(String.class));
                                }else{
                                    tv_act3.setVisibility(View.GONE);
                                }
                            }*//*
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
                            if ("1".equalsIgnoreCase(mood) && moodTopic != null && !moodTopic.trim().isEmpty()) {
                                tv_moodTopic.setText(moodTopic);
                            } else {
                                tv_moodTopic.setVisibility(View.GONE);
                            }
                        }
                        if ("imageUrl".equalsIgnoreCase(userData.getKey())) {
                            for (DataSnapshot children : userData.getChildren()) {
                                if (userId.equalsIgnoreCase(children.getKey())) {
                                    imageUrl = children.getValue().toString();
                                    if (imageUrl != null) {
                                        Glide.with(context).load(imageUrl)
                                                .bitmapTransform(new CropSquareTransformation(context))
                                                .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);
                                    }
                                }
                            }
                        }
                        if ("interests_list".equalsIgnoreCase(userData.getKey())) {
                            userInterests.clear();
                            for (DataSnapshot interest_list : userData.getChildren()) {
                                String key = interest_list.getKey();
                                Map<String, String> topicInterests = interest_list.getValue(Map.class);
                                for (String s : topicInterests.keySet()) {
                                    if ("1".equalsIgnoreCase(topicInterests.get(s))) {
                                        userInterests.add(s);
                                    }
                                }
                            }
                            ArrayList<String> commonInterests = new ArrayList<String>();
                            if (myInterests != null) {
                                int matchCount = 0;
                                commonInterests.clear();
                                for (String s : userInterests) {
                                    if (myInterests.contains(s)) {
                                        matchCount++;
                                        commonInterests.add(s);
                                    }
                                }
                                match = Math.round(((float) matchCount / (myInterests.size())) * 100);
                                if (match == 0) {
                                    tv_matchCount.setText("YOU CLICK 0%");
                                } else if (match % 100 == 0) {
                                    tv_matchCount.setText("YOU CLICK 100%");
                                } else {
                                    tv_matchCount.setText("YOU CLICK " + match % 100 + "%");
                                }
                            }
                            tags.clear();
                            for (String s : commonInterests) {
                                Tag tag = new Tag(s);
                                tag.tagTextColor = Color.parseColor("#F4514E");
                                tag.layoutBorderColor = Color.parseColor("#F4514E");
                                tag.layoutColor = Color.parseColor("#FFFFFF");
                                tag.layoutBorderSize = 1.0F;
                                tag.layoutColorPress = Color.WHITE;
                                tags.add(tag);
                                match++;
                            }
                            tagGroup.addTags(tags);
                        }
                    }
                    String finalInfo = "";
                    if (age != null && !age.isEmpty()) {
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
        }*/


        Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + userId + "/");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    if ("name".equalsIgnoreCase(userData.getKey())) {
                        tv_name.setText(userData.getValue(String.class));
                        userName = userData.getValue(String.class);
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
                            /*if("act3".equalsIgnoreCase(children.getKey())){
                                if(children.getValue(String.class) != null && !children.getValue(String.class).isEmpty()){
                                    tv_act3.setText(children.getValue(String.class));
                                }else{
                                    tv_act3.setVisibility(View.GONE);
                                }
                            }*/
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
                        if ("1".equalsIgnoreCase(mood) && moodTopic != null && !moodTopic.trim().isEmpty()) {
                            tv_moodTopic.setText(moodTopic);
                        } else {
                            tv_moodTopic.setVisibility(View.GONE);
                        }
                    }
                    if ("imageUrl".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if (userId.equalsIgnoreCase(children.getKey())) {
                                imageUrl = children.getValue().toString();
                                if (imageUrl != null) {
                                    Glide.with(context).load(imageUrl)
                                            .bitmapTransform(new CropSquareTransformation(context))
                                            .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);
                                }
                            }
                        }
                    }
                    if ("interests_list".equalsIgnoreCase(userData.getKey())) {
                        userInterests.clear();
                        for (DataSnapshot interest_list : userData.getChildren()) {
                            String key = interest_list.getKey();
                            Map<String, String> topicInterests = interest_list.getValue(Map.class);
                            for (String s : topicInterests.keySet()) {
                                if ("1".equalsIgnoreCase(topicInterests.get(s))) {
                                    userInterests.add(s);
                                }
                            }
                        }
                        ArrayList<String> commonInterests = new ArrayList<String>();
                        if (myInterests != null) {
                            int matchCount = 0;
                            commonInterests.clear();
                            for (String s : userInterests) {
                                if (myInterests.contains(s)) {
                                    matchCount++;
                                    commonInterests.add(s);
                                }
                            }
                            match = Math.round(((float) matchCount / (myInterests.size())) * 100);
                            if (match == 0) {
                                tv_matchCount.setText("YOU CLICK 0%");
                            } else if (match % 100 == 0) {
                                tv_matchCount.setText("YOU CLICK 100%");
                            } else {
                                tv_matchCount.setText("YOU CLICK " + match % 100 + "%");
                            }
                        }
                        tags.clear();
                        for (String s : commonInterests) {
                            Tag tag = new Tag(s);
                            tag.tagTextColor = Color.parseColor("#F4514E");
                            tag.layoutBorderColor = Color.parseColor("#F4514E");
                            tag.layoutColor = Color.parseColor("#FFFFFF");
                            tag.layoutBorderSize = 1.0F;
                            tag.layoutColorPress = Color.WHITE;
                            tags.add(tag);
                            match++;
                        }
                        tagGroup.addTags(tags);
                    }
                }
                String finalInfo = "";
                if (age != null && !age.isEmpty()) {
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

        final String finalUserId1 = userId;
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"You admired "+ userName,Toast.LENGTH_SHORT).show();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/people_i_liked/");
                Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+finalUserId1+"/people_who_liked_me/");
                Firebase likedNotificationRef = new Firebase("https://new-amigos.firebaseio.com/liked_notifications/"
                        +finalUserId1+"/");
                myRef.child(finalUserId1).setValue(timeStamp);
                userRef.child(myId).setValue(timeStamp);
                likedNotificationRef.child(myId).child(myName).setValue(timeStamp);
                final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);

                Firebase userRef1 = new Firebase("https://new-amigos.firebaseio.com/users/"+finalUserId1+"/");
                userRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = null, status = null, imageUrl = null;
                        for (DataSnapshot snap : dataSnapshot.getChildren()){
                            if ("name".equalsIgnoreCase(snap.getKey())) {
                                name = snap.getValue().toString();
                            }
                            if ("status".equalsIgnoreCase(snap.getKey())) {
                                status = snap.getValue().toString();
                            }
                            if ("imageUrl".equalsIgnoreCase(snap.getKey())){
                                for(DataSnapshot children : snap.getChildren()){
                                    if(finalUserId1.equalsIgnoreCase(children.getKey())){
                                        imageUrl = children.getValue().toString();
                                    }
                                }
                            }
                        }
                        peopleILikedDAO.addUserToPeopleILikedList(finalUserId1,name,status,imageUrl);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


                /*DatabaseReference thisRef = FirebaseDatabase.getInstance().getReference();

                thisRef.child("users").child(finalUserId1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = null, status = null, imageUrl = null;
                        for (DataSnapshot snap : dataSnapshot.getChildren()){
                            if ("name".equalsIgnoreCase(snap.getKey())) {
                                name = snap.getValue().toString();
                            }
                            if ("status".equalsIgnoreCase(snap.getKey())) {
                                status = snap.getValue().toString();
                            }
                            if ("imageUrl".equalsIgnoreCase(snap.getKey())){
                                for(DataSnapshot children : snap.getChildren()){
                                    if(finalUserId1.equalsIgnoreCase(children.getKey())){
                                        imageUrl = children.getValue().toString();
                                    }
                                }
                            }
                        }
                        peopleILikedDAO.addUserToPeopleILikedList(finalUserId1,name,status,imageUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }
        });
    }
}
