package com.amigos.sachin.Utils;

import com.amigos.sachin.Values.PeevesList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ADMIN on 10/26/2017.
 */

public class Algorithms {


    public Algorithms(){

    }

    public static int calculateMatch(ArrayList<ArrayList<String>> myInterests, ArrayList<ArrayList<String>> userInterests){
        HashMap<String, String> myMap = createHashMap(myInterests);
        HashMap<String, String> userMap = createHashMap(userInterests);
        int click = calculateClickRate(myMap, userMap);
        return click;
    }

    private static int calculateClickRate(HashMap<String, String> myMap, HashMap<String, String> userMap) {
        ArrayList<String> allInterests = PeevesList.getNewInterestsTopics();
        int count = 0;
        int maxCount = 3*allInterests.size();
        String s1, s2;
        for(String s: allInterests){
            s1 = myMap.get(s);
            s2 = userMap.get(s);
            if(s1 == null){
                s1 = "0";
            }
            if(s2 == null){
                s2 = "0";
            }
            count += 3 - Math.abs(Integer.parseInt(s1) - Integer.parseInt(s2));
        }
        return Math.round(((float) count/maxCount)*100);
    }

    private static HashMap<String, String> createHashMap(ArrayList<ArrayList<String>> interests){
        HashMap<String, String> interestsMap = new HashMap<String, String>();
        for(int i = 0 ; i<interests.size(); i++){
            for(String s: interests.get(i)){
                interestsMap.put(s,String.valueOf(3-i));
            }
        }
        return interestsMap;
    }

}
