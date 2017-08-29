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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;


public class AllUsersFragment extends Fragment {

    ImageView profilePicture;
    TextView tv_name, tv_extra_info, tv_status, tv_act1, tv_act2, tv_act3;
    ArrayList<String> userInterests = new ArrayList<String>();
    ArrayList<String> myInterests = new ArrayList<String>();
    Context context;
    int match = 0;
    TagView tagGroup;
    ArrayList<Tag> tags = new ArrayList<Tag>();
    ImageView messageIcon, likeIcon;
    String myId;

    public AllUsersFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_all_users, container, false);
        Bundle bundle = getArguments();
        context = getActivity();
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        UserVO myVO = ApplicationCache.myUserVO;
        myInterests = myVO.getInterests();
        UserVO userVO = (UserVO) bundle.getSerializable("userData");
        String age = null, sex = null, place = null;
        String userName = null, userId = null, imageUrl = null;

        tv_name = (TextView) view.findViewById(R.id.tv_display_name);
        tv_extra_info = (TextView) view.findViewById(R.id.tv_extra_info) ;
        tv_status = (TextView) view.findViewById(R.id.textView_info);
        tv_act1 = (TextView) view.findViewById(R.id.tv_activity1);
        tv_act2 = (TextView) view.findViewById(R.id.tv_activity2);
        tv_act3 = (TextView) view.findViewById(R.id.tv_activity3);
        profilePicture = (ImageView) view.findViewById(R.id.imageView_profileImage);
        tagGroup = (TagView) view.findViewById(R.id.user_tag_group);
        messageIcon = (ImageView) view.findViewById(R.id.messageIcon);
        likeIcon = (ImageView) view.findViewById(R.id.likeIcon);

        if(userVO.getImageUrl() != null){
            imageUrl = userVO.getImageUrl();
            Glide.with(context).load(userVO.getImageUrl())
                    .bitmapTransform(new CropSquareTransformation(context))
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profilePicture);
        }

        if(userVO.getId() != null){
            userId = userVO.getId();
        }
        if(userVO.getName() != null){
            tv_name.setText(userVO.getName());
            userName = userVO.getName();
        }
        if(userVO.getStatus() != null){
            tv_status.setText(userVO.getStatus());
        }else{
            tv_status.setVisibility(View.GONE);
        }
        if(userVO.getActivity1() != null){
            tv_act1.setText(userVO.getActivity1());
        }else{
            tv_act1.setVisibility(View.GONE);
        }
        if(userVO.getActivity2() != null){
            tv_act2.setText(userVO.getActivity2());
        }else{
            tv_act2.setVisibility(View.GONE);
        }
        if(userVO.getActivity3() != null){
            tv_act3.setText(userVO.getActivity3());
        }else{
            tv_act3.setVisibility(View.GONE);
        }
        if(userVO.getInterests() != null){
            userInterests = userVO.getInterests();
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


        ArrayList<String> commonInterests = new ArrayList<String>();
        if(myInterests != null || !myInterests.isEmpty()){
            int matchCount = 0;
            for(String s : userInterests){
                if(myInterests.contains(s)){
                    matchCount++;
                    commonInterests.add(s);
                }
            }
            match = Math.round(((float)matchCount/(myInterests.size()))*100);
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

        final String finalUserId = userId;
        final String finalUserName = userName;
        final String finalImageUrl = imageUrl;
        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId", finalUserId);
                intent.putExtra("userName", finalUserName);
                intent.putExtra("imageUrl", finalImageUrl);
                startActivity(intent);
            }
        });

        final String finalUserId1 = userId;
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"You liked "+ finalUserName,Toast.LENGTH_SHORT).show();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/people_i_liked/");
                Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+finalUserId1+"/people_who_liked_me/");
                myRef.child(finalUserId1).setValue(timeStamp);
                userRef.child(myId).setValue(timeStamp);
                final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
                Firebase thisUserRef = new Firebase("https://new-amigos.firebaseio.com/users/"+finalUserId1+"/");
                thisUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        PeopleILikedFragment.reloadLikedPeopleList();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });


        return view;
    }

}
