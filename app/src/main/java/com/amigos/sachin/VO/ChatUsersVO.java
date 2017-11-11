package com.amigos.sachin.VO;

/**
 * Created by Sachin on 8/28/2017.
 */

public class ChatUsersVO {
    String myId;
    String userId;
    String lastMessage;
    String time;
    int seen;
    String messageId = "";
    int userSide = 0;

    public int getUserSide() {
        return userSide;
    }

    public void setUserSide(int userSide) {
        this.userSide = userSide;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }
}
