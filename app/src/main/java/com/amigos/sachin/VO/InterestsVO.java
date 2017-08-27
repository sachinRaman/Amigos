package com.amigos.sachin.VO;

/**
 * Created by Sachin on 8/14/2017.
 */

public class InterestsVO {

    String tag;
    String interest;
    int pref;
    public InterestsVO(String tag,String name, int pref){
        this.tag = tag;
        this.interest = name;
        this.pref = pref;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public int getPref() {
        return pref;
    }

    public void setPref(int pref) {
        this.pref = pref;
    }
}
