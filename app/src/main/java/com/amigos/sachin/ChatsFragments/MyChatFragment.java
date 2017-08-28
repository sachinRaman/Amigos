package com.amigos.sachin.ChatsFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatUsersVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MyChatFragment extends Fragment {

    Context context;
    ListView chatListView;
    String myId;


    public MyChatFragment() {

    }

    public static MyChatFragment newInstance(String param1) {
        MyChatFragment fragment = new MyChatFragment();
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

        View view = inflater.inflate(R.layout.fragment_my_chat, container, false);
        context = getContext();
        chatListView = (ListView) view.findViewById(R.id.chatListView);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        ArrayList<ChatUsersVO> chatUsersVOArrayList = chatUsersDAO.getMyChatList(myId);

        Collections.sort(chatUsersVOArrayList, new Comparator<ChatUsersVO>() {
            @Override
            public int compare(ChatUsersVO lhs, ChatUsersVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        ChatLVAdapter chatLVAdapter = new ChatLVAdapter(context,chatUsersVOArrayList,chatListView);
        chatListView.setAdapter(chatLVAdapter);

        return view;
    }

}
