package com.amigos.sachin.VO;

/**
 * Created by sindhusha on 15/5/17.
 */
public class ChatMessageVO {
    public boolean left;
    public String message;
    public String toId;
    public String fromId;
    public String time;
    public String seen;
    public String userId;

    public ChatMessageVO(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
    public ChatMessageVO(boolean left, String message, String time, String seen, String userId) {
        super();
        this.left = left;
        this.message = message;
        this.time = time;
        this.seen = seen;
        this.userId = userId;
    }

    public ChatMessageVO(String toId, String fromId, String message) {
        super();
        this.toId = toId;
        this.fromId=fromId;
        this.message = message;
    }
}