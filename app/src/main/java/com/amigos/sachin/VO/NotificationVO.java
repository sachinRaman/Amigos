package com.amigos.sachin.VO;

import java.util.ArrayList;

/**
 * Created by Sachin on 8/30/2017.
 */

public class NotificationVO {
    public String name;
    public String id;
    public ArrayList<String> messageArrayList = new ArrayList<String>();
    public String time;
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public ArrayList<String> getMessageArrayList() {
        return messageArrayList;
    }

    public void setMessageArrayList(ArrayList<String> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
