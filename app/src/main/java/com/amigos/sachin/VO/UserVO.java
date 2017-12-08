package com.amigos.sachin.VO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sachin on 8/27/2017.
 */

public class UserVO implements Serializable {
    String id;
    String name;
    String age;
    String place;
    String sex;
    String status;
    String activity1;
    String activity2;
    String activity3;
    ArrayList<String> interests = new ArrayList<String>();
    String imageUrl;
    String mood = "0";
    ArrayList<String> myMoodTags = new ArrayList<String>();
    String moodTopic ;
    int match = 0;
    ArrayList<String> peopleIRemoved = new ArrayList<String>();
    ArrayList<String> interests1 = new ArrayList<String>();
    ArrayList<String> interests2 = new ArrayList<String>();
    ArrayList<String> interests3 = new ArrayList<String>();
    ArrayList<String> sentChatRequests = new ArrayList<String>();
    ArrayList<String> pendingChatRequests = new ArrayList<String>();
    ArrayList<String> approvedChatRequests = new ArrayList<String>();
    String fcmToken;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public ArrayList<String> getSentChatRequests() {
        return sentChatRequests;
    }

    public void setSentChatRequests(ArrayList<String> sentChatRequests) {
        this.sentChatRequests = sentChatRequests;
    }

    public ArrayList<String> getPendingChatRequests() {
        return pendingChatRequests;
    }

    public void setPendingChatRequests(ArrayList<String> pendingChatRequests) {
        this.pendingChatRequests = pendingChatRequests;
    }

    public ArrayList<String> getApprovedChatRequests() {
        return approvedChatRequests;
    }

    public void setApprovedChatRequests(ArrayList<String> approvedChatRequests) {
        this.approvedChatRequests = approvedChatRequests;
    }

    public ArrayList<String> getInterests1() {
        return interests1;
    }

    public void setInterests1(ArrayList<String> interests1) {
        this.interests1 = interests1;
    }

    public ArrayList<String> getInterests2() {
        return interests2;
    }

    public void setInterests2(ArrayList<String> interests2) {
        this.interests2 = interests2;
    }

    public ArrayList<String> getInterests3() {
        return interests3;
    }

    public void setInterests3(ArrayList<String> interests3) {
        this.interests3 = interests3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivity1() {
        return activity1;
    }

    public void setActivity1(String acticity1) {
        this.activity1 = acticity1;
    }

    public String getActivity2() {
        return activity2;
    }

    public void setActivity2(String activity2) {
        this.activity2 = activity2;
    }

    public String getActivity3() {
        return activity3;
    }

    public void setActivity3(String activity3) {
        this.activity3 = activity3;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public ArrayList<String> getMyMoodTags() {
        return myMoodTags;
    }

    public void setMyMoodTags(ArrayList<String> myMoodTags) {
        this.myMoodTags = myMoodTags;
    }

    public String getMoodTopic() {
        return moodTopic;
    }

    public void setMoodTopic(String moodTopic) {
        this.moodTopic = moodTopic;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public ArrayList<String> getPeopleIRemoved() {
        return peopleIRemoved;
    }

    public void setPeopleIRemoved(ArrayList<String> peopleIRemoved) {
        this.peopleIRemoved = peopleIRemoved;
    }
}
