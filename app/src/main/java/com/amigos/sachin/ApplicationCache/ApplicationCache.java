package com.amigos.sachin.ApplicationCache;

import android.content.Context;

import com.amigos.sachin.VO.LikedUserVO;
import com.amigos.sachin.VO.UserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sachin on 8/27/2017.
 */

public class ApplicationCache {


    public static Context context;
    public static String myId;
    public static ArrayList<UserVO> userVOArrayList = new ArrayList<UserVO>();
    public static Map<String, UserVO> userVOMap = new HashMap<String, UserVO>();
    public static UserVO myUserVO = new UserVO();
    public static UserVO thisUserVO = new UserVO();
    public static Map<String, UserVO> thisUserVOMap = new HashMap<String, UserVO>();
    public static ArrayList<LikedUserVO> peopleWhoLikedMeVOArrayList = new ArrayList<LikedUserVO>();
    public static ArrayList<LikedUserVO> peopleIRemovedVOArrayList = new ArrayList<LikedUserVO>();
    public static ArrayList<String> peopleIBlockedList = new ArrayList<String>();
    public static ArrayList<String> peopleWhoBlockedMeList = new ArrayList<String>();
    public static ArrayList<LikedUserVO> peopleIBlockedVOList = new ArrayList<LikedUserVO>();
    public static ArrayList<LikedUserVO> peopleWhoBlockedMeVOList = new ArrayList<LikedUserVO>();
    public static ArrayList<LikedUserVO> pendingChatRequestsVO = new ArrayList<LikedUserVO>();

    public ApplicationCache(){
        populateUserVOArrayList();
        loadMyUserVO();
        loadPeopleWhoLikedMeList();
        loadPeopleIRemovedList();
    }

    public static synchronized void populateUserVOArrayList(){
        Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/");
        //DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        //userRef.keepSynced(true);
        /*FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);*/

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserVO> userArrayList = new ArrayList<UserVO>();
                Map<String, UserVO> userMap = new HashMap<String, UserVO>();

                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    String id, name, age, place, sex, status, activity1, activity2, activity3, imageUrl, fcmToken;
                    ArrayList<String> interests = new ArrayList<String>();

                    UserVO userVO = new UserVO();
                    id = snap.getKey();
                    userVO.setId(id);

                    for (DataSnapshot userData : snap.getChildren()){
                        if ("name".equalsIgnoreCase(userData.getKey())){
                            name = userData.getValue(String.class);
                            userVO.setName(name);
                        }
                        if ("age".equalsIgnoreCase(userData.getKey())){
                            age = userData.getValue(String.class);
                            userVO.setAge(age);
                        }
                        if ("place".equalsIgnoreCase(userData.getKey())){
                            place = userData.getValue(String.class);
                            userVO.setPlace(place);
                        }
                        if ("sex".equalsIgnoreCase(userData.getKey())){
                            sex = userData.getValue(String.class);
                            userVO.setSex(sex);
                        }
                        if ("status".equalsIgnoreCase(userData.getKey())){
                            status = userData.getValue(String.class);
                            userVO.setStatus(status);
                        }
                        if ("activity".equalsIgnoreCase(userData.getKey())) {
                            for (DataSnapshot children : userData.getChildren()){
                                if("act1".equalsIgnoreCase(children.getKey())){
                                    activity1 = children.getValue(String.class);
                                    userVO.setActivity1(activity1);
                                }
                                if("act2".equalsIgnoreCase(children.getKey())){
                                    activity2 = children.getValue(String.class);
                                    userVO.setActivity2(activity2);
                                }
                                if("act3".equalsIgnoreCase(children.getKey())){
                                    activity3 = children.getValue(String.class);
                                    userVO.setActivity3(activity3);
                                }
                            }
                        }
                        if ("imageUrl".equalsIgnoreCase(userData.getKey())){
                            for(DataSnapshot children : userData.getChildren()){
                                if(id.equalsIgnoreCase(children.getKey())){
                                    imageUrl = children.getValue().toString();
                                    userVO.setImageUrl(imageUrl);
                                }
                            }
                        }
                        if("interests_list".equalsIgnoreCase(userData.getKey())){
                            for (DataSnapshot interest_list : userData.getChildren()){
                                String key = interest_list.getKey();
                                for(DataSnapshot thisTopicOfInterest : interest_list.getChildren()){
                                    if("1".equalsIgnoreCase(thisTopicOfInterest.getValue().toString())){
                                        interests.add(thisTopicOfInterest.getKey());
                                    }
                                }
                            }
                            userVO.setInterests(interests);

                        }
                        if("interests".equalsIgnoreCase(userData.getKey())){
                            for (DataSnapshot interest_list : userData.getChildren()){
                                String key = interest_list.getKey();

                                ArrayList<String> interests1 = new ArrayList<String>();
                                ArrayList<String> interests2 = new ArrayList<String>();
                                ArrayList<String> interests3 = new ArrayList<String>();

                                if("interests1".equalsIgnoreCase(key)){
                                    for(DataSnapshot snap1 : interest_list.getChildren()){
                                        interests1.add(snap1.getKey());
                                    }
                                    userVO.setInterests1(interests1);
                                }
                                if("interests2".equalsIgnoreCase(key)){
                                    for(DataSnapshot snap1 : interest_list.getChildren()){
                                        interests2.add(snap1.getKey());
                                    }
                                    userVO.setInterests2(interests2);
                                }
                                if("interests3".equalsIgnoreCase(key)){
                                    for(DataSnapshot snap1 : interest_list.getChildren()){
                                        interests3.add(snap1.getKey());
                                    }
                                    userVO.setInterests3(interests3);
                                }
                            }

                        }
                        if("moods".equalsIgnoreCase(userData.getKey())){
                            for (DataSnapshot children : userData.getChildren()){
                                if("interests".equalsIgnoreCase(children.getKey())){
                                    ArrayList<String> myMoodsTags = new ArrayList<String>();
                                    for (DataSnapshot snap1 : children.getChildren()){
                                        if("1".equalsIgnoreCase(snap1.getValue().toString())){
                                            myMoodsTags.add(snap1.getKey());
                                        }
                                    }
                                    userVO.setMyMoodTags(myMoodsTags);
                                }
                                if("topic".equalsIgnoreCase(children.getKey())){
                                    userVO.setMoodTopic(children.getValue().toString());
                                }
                                if("mood".equalsIgnoreCase(children.getKey())){
                                    userVO.setMood(children.getValue().toString());
                                }
                            }
                        }
                        if("fcmToken".equalsIgnoreCase(userData.getKey())){
                            fcmToken = userData.getValue().toString();
                            userVO.setFcmToken(fcmToken);
                        }

                    }
                    userArrayList.add(userVO);
                    userMap.put(id,userVO);
                }
                setUserVOArrayList(userArrayList,userMap);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private static void setUserVOArrayList(ArrayList<UserVO> userArrayList, Map<String, UserVO> userMap){
        userVOArrayList = userArrayList;
        userVOMap = userMap;
    }

    public static void setMyId(String id){
        myId = id;
    }

    public static synchronized void loadMyUserVO(){
        final Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()){
                    String name, age, place, sex, status, activity1, activity2, activity3, imageUrl;
                    ArrayList<String> interests = new ArrayList<String>();

                    myUserVO.setId(myId);

                    if ("name".equalsIgnoreCase(userData.getKey())){
                        name = userData.getValue(String.class);
                        myUserVO.setName(name);
                    }
                    if ("age".equalsIgnoreCase(userData.getKey())){
                        age = userData.getValue(String.class);
                        myUserVO.setAge(age);
                    }
                    if ("place".equalsIgnoreCase(userData.getKey())){
                        place = userData.getValue(String.class);
                        myUserVO.setPlace(place);
                    }
                    if ("sex".equalsIgnoreCase(userData.getKey())){
                        sex = userData.getValue(String.class);
                        myUserVO.setSex(sex);
                    }
                    if ("block_list".equalsIgnoreCase(userData.getKey())){
                        for (DataSnapshot children : userData.getChildren()){
                            if ("people_i_blocked".equalsIgnoreCase(children.getKey())){
                                peopleIBlockedList.clear();
                                peopleIBlockedVOList.clear();
                                for(DataSnapshot snap : children.getChildren()){
                                    LikedUserVO likedUserVO = new LikedUserVO();
                                    likedUserVO.setId(snap.getKey().toString());
                                    likedUserVO.setTime(snap.getValue().toString());
                                    peopleIBlockedVOList.add(likedUserVO);
                                    peopleIBlockedList.add(snap.getKey().toString());
                                }
                            }
                            if ("people_who_blocked_me".equalsIgnoreCase(children.getKey())){
                                peopleWhoBlockedMeList.clear();
                                peopleWhoBlockedMeVOList.clear();
                                for(DataSnapshot snap : children.getChildren()){
                                    LikedUserVO likedUserVO = new LikedUserVO();
                                    likedUserVO.setId(snap.getKey().toString());
                                    likedUserVO.setTime(snap.getValue().toString());
                                    peopleWhoBlockedMeVOList.add(likedUserVO);
                                    peopleWhoBlockedMeList.add(snap.getKey().toString());
                                }
                            }
                        }
                    }
                    if("peopleIRemoved".equalsIgnoreCase(userData.getKey())){
                        ArrayList<String> peopleIRemoved = new ArrayList<String>();
                        peopleIRemoved.clear();
                        for(DataSnapshot children : userData.getChildren()){
                            if("1".equalsIgnoreCase(children.getValue().toString())){
                                peopleIRemoved.add(children.getKey());
                            }
                        }
                        myUserVO.setPeopleIRemoved(peopleIRemoved);
                    }
                    if ("status".equalsIgnoreCase(userData.getKey())){
                        status = userData.getValue(String.class);
                        myUserVO.setStatus(status);
                    }
                    if ("activity".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()){
                            if("act1".equalsIgnoreCase(children.getKey())){
                                activity1 = children.getValue(String.class);
                                myUserVO.setActivity1(activity1);
                            }
                            if("act2".equalsIgnoreCase(children.getKey())){
                                activity2 = children.getValue(String.class);
                                myUserVO.setActivity2(activity2);
                            }
                            if("act3".equalsIgnoreCase(children.getKey())){
                                activity3 = children.getValue(String.class);
                                myUserVO.setActivity3(activity3);
                            }
                        }
                    }
                    if ("imageUrl".equalsIgnoreCase(userData.getKey())){
                        for(DataSnapshot children : userData.getChildren()){
                            if(myId.equalsIgnoreCase(children.getKey())){
                                imageUrl = children.getValue().toString();
                                myUserVO.setImageUrl(imageUrl);
                            }
                        }
                    }
                    if("interests_list".equalsIgnoreCase(userData.getKey())){
                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();
                            for(DataSnapshot thisTopicOfInterest : interest_list.getChildren()){
                                if("1".equalsIgnoreCase(thisTopicOfInterest.getValue().toString())){
                                    interests.add(thisTopicOfInterest.getKey());
                                }
                            }
                        }
                        myUserVO.setInterests(interests);
                    }
                    if("interests".equalsIgnoreCase(userData.getKey())){
                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();

                            ArrayList<String> interests1 = new ArrayList<String>();
                            ArrayList<String> interests2 = new ArrayList<String>();
                            ArrayList<String> interests3 = new ArrayList<String>();

                            if("interests1".equalsIgnoreCase(key)){
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests1.add(snap1.getKey());
                                }
                                myUserVO.setInterests1(interests1);
                            }
                            if("interests2".equalsIgnoreCase(key)){
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests2.add(snap1.getKey());
                                }
                                myUserVO.setInterests2(interests2);
                            }
                            if("interests3".equalsIgnoreCase(key)){
                                for(DataSnapshot snap1 : interest_list.getChildren()){
                                    interests3.add(snap1.getKey());
                                }
                                myUserVO.setInterests3(interests3);
                            }
                        }

                    }
                    if("moods".equalsIgnoreCase(userData.getKey())){
                        for (DataSnapshot children : userData.getChildren()){
                            if("interests".equalsIgnoreCase(children.getKey())){
                                ArrayList<String> myMoodsTags = new ArrayList<String>();
                                for (DataSnapshot snap : children.getChildren()){
                                    if("1".equalsIgnoreCase(snap.getValue().toString())){
                                        myMoodsTags.add(snap.getKey());
                                    }
                                }
                                myUserVO.setMyMoodTags(myMoodsTags);
                            }
                            if("topic".equalsIgnoreCase(children.getKey())){
                                myUserVO.setMoodTopic(children.getValue().toString());
                            }
                            if("mood".equalsIgnoreCase(children.getKey())){
                                myUserVO.setMood(children.getValue().toString());
                            }
                        }
                    }
                    if("chat_requests".equalsIgnoreCase(userData.getKey())){
                        pendingChatRequestsVO.clear();
                        ArrayList<String> sentRequests = new ArrayList<String>();
                        ArrayList<String> pendingRequests = new ArrayList<String>();
                        ArrayList<String> approvedRequests = new ArrayList<String>();
                        String timeStamp = "";

                        for (DataSnapshot children : userData.getChildren()){

                            String userId = children.getKey();
                            for(DataSnapshot snapshot : children.getChildren()){
                                if("status".equalsIgnoreCase(snapshot.getKey())){

                                    if("0".equalsIgnoreCase(snapshot.getValue().toString())){
                                        sentRequests.add(userId);
                                    }
                                    if("1".equalsIgnoreCase(snapshot.getValue().toString())){
                                        pendingRequests.add(userId);
                                    }
                                    if("2".equalsIgnoreCase(snapshot.getValue().toString())){
                                        approvedRequests.add(userId);
                                    }
                                }
                                if("time".equalsIgnoreCase(snapshot.getKey())){
                                    timeStamp = snapshot.getValue().toString();
                                }
                            }
                            if(pendingRequests.contains(userId)) {
                                LikedUserVO likedUserVO = new LikedUserVO();
                                likedUserVO.setTime(timeStamp);
                                likedUserVO.setId(userId);
                                likedUserVO.setName("");
                                likedUserVO.setImageUrl("");
                                likedUserVO.setStatus("");
                                pendingChatRequestsVO.add(likedUserVO);
                            }

                        }
                        myUserVO.setSentChatRequests(sentRequests);
                        myUserVO.setPendingChatRequests(pendingRequests);
                        myUserVO.setApprovedChatRequests(approvedRequests);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static synchronized void loadPeopleWhoLikedMeList(){

        Firebase myLikeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/people_who_liked_me/");
        myLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleWhoLikedMeVOArrayList.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String userId = child.getKey();
                    String time = child.getValue().toString();
                    LikedUserVO likedUserVO = new LikedUserVO();
                    likedUserVO.setTime(time);
                    likedUserVO.setId(userId);
                    likedUserVO.setName("");
                    likedUserVO.setImageUrl("");
                    likedUserVO.setStatus("");
                    peopleWhoLikedMeVOArrayList.add(likedUserVO);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static synchronized void loadPeopleIRemovedList(){

        Firebase myLikeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/peopleIremoved/");
        myLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleIRemovedVOArrayList.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String userId = child.getKey();
                    String time = child.getValue().toString();
                    LikedUserVO likedUserVO = new LikedUserVO();
                    likedUserVO.setTime(time);
                    likedUserVO.setId(userId);
                    likedUserVO.setName("");
                    likedUserVO.setImageUrl("");
                    likedUserVO.setStatus("");
                    peopleIRemovedVOArrayList.add(likedUserVO);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static synchronized void loadThisUserVO(final String userId) {
        final Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + userId + "/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    String name, age, place, sex, status, activity1, activity2, activity3, imageUrl;
                    ArrayList<String> interests = new ArrayList<String>();

                    thisUserVO.setId(userId);

                    if ("name".equalsIgnoreCase(userData.getKey())) {
                        name = userData.getValue(String.class);
                        thisUserVO.setName(name);
                    }
                    if ("age".equalsIgnoreCase(userData.getKey())) {
                        age = userData.getValue(String.class);
                        thisUserVO.setAge(age);
                    }
                    if ("place".equalsIgnoreCase(userData.getKey())) {
                        place = userData.getValue(String.class);
                        thisUserVO.setPlace(place);
                    }
                    if ("sex".equalsIgnoreCase(userData.getKey())) {
                        sex = userData.getValue(String.class);
                        thisUserVO.setSex(sex);
                    }
                    if ("block_list".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if ("people_i_blocked".equalsIgnoreCase(children.getKey())) {
                                peopleIBlockedList.clear();
                                peopleIBlockedVOList.clear();
                                for (DataSnapshot snap : children.getChildren()) {
                                    LikedUserVO likedUserVO = new LikedUserVO();
                                    likedUserVO.setId(snap.getKey().toString());
                                    likedUserVO.setTime(snap.getValue().toString());
                                    peopleIBlockedVOList.add(likedUserVO);
                                    peopleIBlockedList.add(snap.getKey().toString());
                                }
                            }
                            if ("people_who_blocked_me".equalsIgnoreCase(children.getKey())) {
                                peopleWhoBlockedMeList.clear();
                                peopleWhoBlockedMeVOList.clear();
                                for (DataSnapshot snap : children.getChildren()) {
                                    LikedUserVO likedUserVO = new LikedUserVO();
                                    likedUserVO.setId(snap.getKey().toString());
                                    likedUserVO.setTime(snap.getValue().toString());
                                    peopleWhoBlockedMeVOList.add(likedUserVO);
                                    peopleWhoBlockedMeList.add(snap.getKey().toString());
                                }
                            }
                        }
                    }
                    if ("status".equalsIgnoreCase(userData.getKey())) {
                        status = userData.getValue(String.class);
                        thisUserVO.setStatus(status);
                    }
                    if ("activity".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if ("act1".equalsIgnoreCase(children.getKey())) {
                                activity1 = children.getValue(String.class);
                                thisUserVO.setActivity1(activity1);
                            }
                            if ("act2".equalsIgnoreCase(children.getKey())) {
                                activity2 = children.getValue(String.class);
                                thisUserVO.setActivity2(activity2);
                            }
                            /*if ("act3".equalsIgnoreCase(children.getKey())) {
                                activity3 = children.getValue(String.class);
                                thisUserVO.setActivity3(activity3);
                            }*/
                        }
                    }
                    if ("imageUrl".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if (myId.equalsIgnoreCase(children.getKey())) {
                                imageUrl = children.getValue().toString();
                                thisUserVO.setImageUrl(imageUrl);
                            }
                        }
                    }
                    if ("interests_list".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot interest_list : userData.getChildren()) {
                            String key = interest_list.getKey();
                            for (DataSnapshot thisTopicOfInterest : interest_list.getChildren()) {
                                if ("1".equalsIgnoreCase(thisTopicOfInterest.getValue().toString())) {
                                    interests.add(thisTopicOfInterest.getKey());
                                }
                            }
                        }
                        thisUserVO.setInterests(interests);
                    }
                    if ("moods".equalsIgnoreCase(userData.getKey())) {
                        for (DataSnapshot children : userData.getChildren()) {
                            if ("interests".equalsIgnoreCase(children.getKey())) {
                                ArrayList<String> myMoodsTags = new ArrayList<String>();
                                for (DataSnapshot snap : children.getChildren()) {
                                    if ("1".equalsIgnoreCase(snap.getValue().toString())) {
                                        myMoodsTags.add(snap.getKey());
                                    }
                                }
                                thisUserVO.setMyMoodTags(myMoodsTags);
                            }
                            if ("topic".equalsIgnoreCase(children.getKey())) {
                                thisUserVO.setMoodTopic(children.getValue().toString());
                            }
                            if ("mood".equalsIgnoreCase(children.getKey())) {
                                thisUserVO.setMood(children.getValue().toString());
                            }
                        }
                    }
                }
                thisUserVOMap.put(userId,thisUserVO);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
