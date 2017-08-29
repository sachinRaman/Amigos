package com.amigos.sachin.ChatsFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
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

        final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
        ArrayList<LikedUserVO> peopleILikedVOArrayList = peopleILikedDAO.getAllPeopleIliked();



        Collections.sort(peopleILikedVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleILikedVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
        return view;
    }

    public static void reloadLikedPeopleList(){
        final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
        ArrayList<LikedUserVO> peopleILikedVOArrayList = peopleILikedDAO.getAllPeopleIliked();



        Collections.sort(peopleILikedVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleILikedVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        final PeopleILikedDAO peopleILikedDAO = new PeopleILikedDAO(context);
        ArrayList<LikedUserVO> peopleILikedVOArrayList = peopleILikedDAO.getAllPeopleIliked();



        Collections.sort(peopleILikedVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleILikedVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);
    }
}
