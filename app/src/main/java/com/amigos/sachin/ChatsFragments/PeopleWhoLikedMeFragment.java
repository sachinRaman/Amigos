package com.amigos.sachin.ChatsFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.PeopleILikedDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleWhoLikedMeFragment extends Fragment {

    static Context context;
    static ListView likedListView;
    static String myId;
    static LikedUsersLVAdapter likedLVAdapter;

    public PeopleWhoLikedMeFragment() {
        // Required empty public constructor
    }

    public static PeopleWhoLikedMeFragment newInstance(String param1) {
        PeopleWhoLikedMeFragment fragment = new PeopleWhoLikedMeFragment();
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

        View view =  inflater.inflate(R.layout.fragment_people_who_liked_me, container, false);
        context = getContext();
        likedListView = (ListView) view.findViewById(R.id.peopleWhoLikedMeListView);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        ArrayList<LikedUserVO> peopleWhoLikedMeVOArrayList = ApplicationCache.peopleWhoLikedMeVOArrayList;

        Collections.sort(peopleWhoLikedMeVOArrayList, new Comparator<LikedUserVO>() {
            @Override
            public int compare(LikedUserVO lhs, LikedUserVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        likedLVAdapter = new LikedUsersLVAdapter(context,peopleWhoLikedMeVOArrayList,likedListView);
        likedListView.setAdapter(likedLVAdapter);

        return view;
    }

}
