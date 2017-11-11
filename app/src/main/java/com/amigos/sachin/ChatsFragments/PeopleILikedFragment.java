package com.amigos.sachin.ChatsFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PeopleILikedFragment extends Fragment {

    static Context context;
    static ListView likedListView;
    static String myId;
    static LikedUsersLVAdapter likedLVAdapter;
    static TextView tv_noLikesTextView;

    public PeopleILikedFragment() {

    }

    public static PeopleILikedFragment newInstance(String param1) {
        PeopleILikedFragment fragment = new PeopleILikedFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people_iliked, container, false);
        context = getContext();
        likedListView = (ListView) view.findViewById(R.id.peopleILikedListView);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        tv_noLikesTextView = (TextView) view.findViewById(R.id.tv_noLikesTextView);

        reloadPeopleILikedList();



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        reloadPeopleILikedList();
    }

    public static void reloadPeopleILikedList() {
        ArrayList<LikedUserVO> peopleWhoLikedMeVOArrayList = ApplicationCache.pendingChatRequestsVO;

        ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        for( int i = peopleWhoLikedMeVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = peopleWhoLikedMeVOArrayList.get(i).getId();
            if( peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId) ){
                peopleWhoLikedMeVOArrayList.remove(i);
            }
        }

        Collections.sort(peopleWhoLikedMeVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        if (peopleWhoLikedMeVOArrayList == null || peopleWhoLikedMeVOArrayList.isEmpty()){
            likedListView.setVisibility(View.GONE);
            tv_noLikesTextView.setVisibility(View.VISIBLE);
        }else{
            likedListView.setVisibility(View.VISIBLE);
            tv_noLikesTextView.setVisibility(View.GONE);
        }

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleWhoLikedMeVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
    }

    /*public static void reloadPeopleILikedList() {
        final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
        ArrayList<LikedUserVO> peopleILikedVOArrayList = peopleILikedDAO.getAllPeopleIliked();

        ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        for( int i = peopleILikedVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = peopleILikedVOArrayList.get(i).getId();
            if( peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId) ){
                peopleILikedVOArrayList.remove(i);
            }
        }
        Collections.sort(peopleILikedVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        if (peopleILikedVOArrayList.isEmpty()){
            likedListView.setVisibility(View.GONE);
            tv_noLikesTextView.setVisibility(View.VISIBLE);
        }else{
            likedListView.setVisibility(View.VISIBLE);
            tv_noLikesTextView.setVisibility(View.GONE);
        }

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleILikedVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
    }*/
}
