package com.amigos.sachin.AllUsersFragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.Activities.ChatActivity;
import com.amigos.sachin.Activities.ZoomedProfileImageActivity;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.ChatNotificationsDAO;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.MainFragments.UsersFragment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;


public class AllUsersFragment extends Fragment {

    ImageView profilePicture;
    TextView  tv_matchCount, tv_professionalProfileText,
            tv_InterestedInText,tv_moodTopic,tv_moodTopicTextView, tv_admired_you, tv_bullet;
    EmojiconTextView tv_name, tv_extra_info, tv_status, tv_act1, tv_act2;
    ArrayList<String> userInterests = new ArrayList<String>();
    ArrayList<String> myInterests = new ArrayList<String>();
    Context context;
    int match = 0;
    //TagView tags_interests_general, tags_interests_arts, tags_interests_entertainment, tags_interests_literature, tags_interests_business, tags_interests_sports,
    //tags_interests_music, tags_interests_science;
    TextView tv_click_general, tv_click_arts, tv_click_entertainment, tv_click_literature, tv_click_business,
            tv_click_sports, tv_click_music, tv_click_science;

    TextView tv_interests1, tv_interests2, tv_interests3;
    TagView tags_interests1, tags_interests2, tags_interests3;

    ArrayList<Tag> tags = new ArrayList<Tag>();
    ImageView messageIcon, likeIcon, crossIcon, check_icon;
    //ImageView swipeLeftIcon, swipeRightIcon;
    String myId, userImageUrl;
    String userFcmToken;
    String userName = null, userId = null, imageUrl = null;

    public AllUsersFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_all_users, container, false);
        Bundle bundle = getArguments();
        context = getActivity();

        Firebase.setAndroidContext(context);

        final SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        final String myName = ApplicationCache.myUserVO.getName();

        UserVO myVO = ApplicationCache.myUserVO;
        myInterests = myVO.getInterests();
        final UserVO userVO = (UserVO) bundle.getSerializable("userData");
        final int position = bundle.getInt("position");
        String age = null, sex = null, place = null;


        /*linear_layout_search = (LinearLayout) view.findViewById(R.id.linear_layout_search);
        et_search_all_users = (EditText) view.findViewById(R.id.et_search_all_users);*/
        tv_name = (EmojiconTextView) view.findViewById(R.id.tv_display_name);
        tv_extra_info = (EmojiconTextView) view.findViewById(R.id.tv_extra_info) ;
        tv_status = (EmojiconTextView) view.findViewById(R.id.textView_info);
        tv_act1 = (EmojiconTextView) view.findViewById(R.id.tv_activity1);
        tv_act2 = (EmojiconTextView) view.findViewById(R.id.tv_activity2);

        tv_name.setEmojiconSize(40);
        tv_extra_info.setEmojiconSize(40);
        tv_status.setEmojiconSize(40);
        tv_act1.setEmojiconSize(40);
        tv_act2.setEmojiconSize(40);

        profilePicture = (ImageView) view.findViewById(R.id.imageView_profileImage);
        messageIcon = (ImageView) view.findViewById(R.id.messageIcon);
        likeIcon = (ImageView) view.findViewById(R.id.likeIcon);
        crossIcon = (ImageView) view.findViewById(R.id.crossIcon);
        check_icon = (ImageView) view.findViewById(R.id.check_icon);
        //swipeLeftIcon = (ImageView) view.findViewById(R.id.swipeLeftIcon);
        //swipeRightIcon = (ImageView) view.findViewById(R.id.swipeRightIcon);
        tv_InterestedInText = (TextView) view.findViewById(R.id.tv_interestedActivities);
        tv_professionalProfileText = (TextView) view.findViewById(R.id.tv_professionalProfile);
        tv_admired_you = (TextView) view.findViewById(R.id.tv_admired_you);
        tv_bullet = (TextView) view.findViewById(R.id.tv_bullet);
        tv_matchCount = (TextView) view.findViewById(R.id.tv_click);

        tv_interests1 = (TextView) view.findViewById(R.id.tv_interests1);
        tv_interests2 = (TextView) view.findViewById(R.id.tv_interests2);
        tv_interests3 = (TextView) view.findViewById(R.id.tv_interests3);

        tags_interests1 = (TagView) view.findViewById(R.id.tags_interests1);
        tags_interests2 = (TagView) view.findViewById(R.id.tags_interests2);
        tags_interests3 = (TagView) view.findViewById(R.id.tags_interests3);

        if(userVO.getId() != null){
            userId = userVO.getId();
            String finalUserId = userId;
        }

        userFcmToken = userVO.getFcmToken();

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
                if(myAdmirers.contains(userId)){
                    tv_bullet.setVisibility(View.VISIBLE);
                    tv_admired_you.setVisibility(View.VISIBLE);
                    if (match == 0){
                        tv_bullet.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if(userVO.getImageUrl() != null){
            imageUrl = userVO.getImageUrl();
            userImageUrl = imageUrl;
            if(!imageUrl.isEmpty()) {
                Glide.with(context).load(userVO.getImageUrl()).error(R.drawable.ic_user)
                        .bitmapTransform(new CropSquareTransformation(context), new CropCircleTransformation(context))
                        .thumbnail(0.01f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);
            }
        }


        if(userVO.getName() != null){
            tv_name.setText(userVO.getName());
            userName = userVO.getName();
        }
        if(userVO.getStatus() != null && !userVO.getStatus().trim().isEmpty()){
            tv_status.setText(userVO.getStatus());
        }else{
            tv_status.setVisibility(View.GONE);
        }
        if(userVO.getActivity1() != null && !userVO.getActivity1().trim().isEmpty()){
            tv_act1.setText(userVO.getActivity1());
        }else{
            tv_act1.setVisibility(View.GONE);
            tv_InterestedInText.setVisibility(View.GONE);
        }
        if(userVO.getActivity2() != null && !userVO.getActivity2().trim().isEmpty()){
            tv_act2.setText(userVO.getActivity2());
        }else{
            tv_act2.setVisibility(View.GONE);
            tv_professionalProfileText.setVisibility(View.GONE);
        }

        if(userVO.getInterests() != null){
            userInterests = userVO.getInterests();
        }
        match = userVO.getMatch();
        if (match == 0){
            tv_matchCount.setText("Clicks 0%");
            tv_matchCount.setVisibility(View.GONE);
            tv_bullet.setVisibility(View.GONE);
        }else if(match%100 == 0){
            tv_matchCount.setVisibility(View.VISIBLE);
            tv_matchCount.setText("Clicks 100%");
        }else{
            tv_matchCount.setVisibility(View.VISIBLE);
            tv_matchCount.setText("Clicks " + userVO.getMatch() % 100 + "%");
        }
        if(userVO.getAge() != null){
            age = userVO.getAge();
        }
        if(userVO.getSex() != null){
            sex = userVO.getSex();
        }
        if(userVO.getPlace() != null){
            place = userVO.getPlace();
        }
        String finalInfo = "";
        if( age != null && !age.isEmpty() && isNumeric(age.trim()) ){
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

        updateTags(tv_interests1, tags_interests1, userVO.getInterests1());
        updateTags(tv_interests2, tags_interests2, userVO.getInterests2());
        updateTags(tv_interests3, tags_interests3, userVO.getInterests3());


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ZoomedProfileImageActivity.class);

                intent.putExtra("imageUrl",userVO.getImageUrl());

                startActivity(intent);

                // Get the transition name from the string
                /*String transitionName = getString(R.string.zoom_transition_string);

                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
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
        if(peopleILikedArrayList.contains(userId)){
            check_icon.setVisibility(View.VISIBLE);
        }

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
                ArrayList<String> peopleILikedArrayList = peopleILikedDAO.getAllPeopleIlikedId();
                if(peopleILikedArrayList.contains(userId)){
                    check_icon.setVisibility(View.GONE);
                    Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/people_i_liked/");
                    Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + AllUsersFragment.this.userId + "/people_who_liked_me/");
                    myRef.child(AllUsersFragment.this.userId).setValue(null);
                    userRef.child(myId).setValue(null);
                    peopleILikedDAO.removeFromPeopleILikedList(AllUsersFragment.this.userId);
                    Toast.makeText(context,"You have unadmired "+ userName,Toast.LENGTH_SHORT).show();
                }else {
                    check_icon.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "You admired " + userName, Toast.LENGTH_SHORT).show();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/people_i_liked/");
                    Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/" + AllUsersFragment.this.userId + "/people_who_liked_me/");
                    Firebase likedNotificationRef = new Firebase("https://new-amigos.firebaseio.com/liked_notifications/"
                            + AllUsersFragment.this.userId + "/");

                    Firebase admiredNotificationRef = new Firebase("https://new-amigos.firebaseio.com/admired_notifications/"
                            + AllUsersFragment.this.userId + "/"+myId+"/");
                    myRef.child(AllUsersFragment.this.userId).setValue(timeStamp);
                    userRef.child(myId).setValue(timeStamp);
                    likedNotificationRef.child(myId).child(myName).setValue(timeStamp);
                    admiredNotificationRef.child("name").setValue(myName);
                    admiredNotificationRef.child("timeStamp").setValue(timeStamp);
                    admiredNotificationRef.child("fcmToken").setValue(userFcmToken);

                    Firebase thisUserRef = new Firebase("https://new-amigos.firebaseio.com/users/" + AllUsersFragment.this.userId + "/");
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
                                        if (AllUsersFragment.this.userId.equalsIgnoreCase(children.getKey())) {
                                            imageUrl = children.getValue().toString();
                                        }
                                    }
                                }
                            }
                            peopleILikedDAO.addUserToPeopleILikedList(AllUsersFragment.this.userId, name, status, imageUrl);
                            //PeopleILikedFragment.reloadPeopleILikedList();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

            }
        });

        crossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SwipeAllUsersAdapter.userVOArrayList.remove(position);
                UsersFragment.updateSwipeUsersAdapter(position);
                Firebase peopleIRemovedRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/peopleIremoved/");
                peopleIRemovedRef.child(userId ).setValue("1");
                ApplicationCache.myUserVO.getPeopleIRemoved().add(userId);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                LikedUserVO likedUserVO = new LikedUserVO();
                likedUserVO.setTime(timeStamp);
                likedUserVO.setId(userId);
                likedUserVO.setName(userName);
                likedUserVO.setImageUrl(userImageUrl);
                likedUserVO.setStatus("");

                ApplicationCache.peopleIRemovedVOArrayList.add(likedUserVO);*/

                if(position < 39){
                    UsersFragment.swipeRight();
                }else{
                    Toast.makeText(context, "There are no more users to show right now, to see more you can change"+
                            " your preferences",Toast.LENGTH_SHORT).show();
                }

                /*final Firebase usersRef = new Firebase("https://new-amigos.firebaseio.com/users/");
                //final Firebase chatsRef = new Firebase("https://new-amigos.firebaseio.com/previous_user_chats/");

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String id = snapshot.getKey();
                            Firebase fromPath = usersRef.child(id).child("chats");
                            fromPath.setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/

            }
        });

        /*swipeLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersFragment.swipeLeft();
            }
        });
        swipeRightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersFragment.swipeRight();
            }
        });*/

        return view;
    }

    public void moveFirebaseRecord(Firebase fromPath, final Firebase toPath)
    {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue(), new Firebase.CompletionListener()
                {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase)
                    {
                        if (firebaseError != null)
                        {
                            System.out.println("Copy failed");
                        }
                        else
                        {
                            System.out.println("Success");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError)
            {
                System.out.println("Copy failed");
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
