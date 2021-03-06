package com.amigos.sachin.ChatsFragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatUsersVO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;


public class MyChatFragment extends Fragment {

    static Context context;
    static ListView chatListView;
    static String myId;
    static ChatLVAdapter chatLVAdapter;
    static TextView tv_emptyChat;
    public static View v;


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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            reloadChat();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_chat, container, false);
        context = getContext();
        v = view;
        chatListView = (ListView) view.findViewById(R.id.chatListView);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        tv_emptyChat = (TextView) view.findViewById(R.id.tv_emptyChat);

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("reloadChatList"));

        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        ArrayList<ChatUsersVO> chatUsersVOArrayList = chatUsersDAO.getMyChatList(myId);

        ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        for( int i = chatUsersVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = chatUsersVOArrayList.get(i).getUserId();
            if( peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId) ){
                chatUsersVOArrayList.remove(i);
            }
        }

        Collections.sort(chatUsersVOArrayList, new Comparator<ChatUsersVO>() {
            @Override
            public int compare(ChatUsersVO lhs, ChatUsersVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        if (chatUsersVOArrayList.isEmpty()){
            chatListView.setVisibility(View.GONE);
            tv_emptyChat.setVisibility(View.VISIBLE);
        }else{
            chatListView.setVisibility(View.VISIBLE);
            tv_emptyChat.setVisibility(View.GONE);
        }

        chatLVAdapter = new ChatLVAdapter(context,chatUsersVOArrayList,chatListView);
        chatListView.setAdapter(chatLVAdapter);

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    public static void reloadChat(){

        if (context == null || myId == null){
            return;
        }

        try {
            ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
            ArrayList<ChatUsersVO> chatUsersVOArrayList = chatUsersDAO.getMyChatList(myId);

            ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
            ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

            for (int i = chatUsersVOArrayList.size() - 1; i >= 0; i--) {
                String userId = chatUsersVOArrayList.get(i).getUserId();
                if (peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId)) {
                    chatUsersVOArrayList.remove(i);
                }
            }

            Collections.sort(chatUsersVOArrayList, new Comparator<ChatUsersVO>() {
                @Override
                public int compare(ChatUsersVO lhs, ChatUsersVO rhs) {
                    if (lhs.getTime().compareTo(rhs.getTime()) > 0)
                        return -1;
                    return 1;
                }
            });

            if (chatUsersVOArrayList.isEmpty()) {
                chatListView.setVisibility(View.GONE);
                tv_emptyChat.setVisibility(View.VISIBLE);
            } else {
                chatListView.setVisibility(View.VISIBLE);
                tv_emptyChat.setVisibility(View.GONE);
            }

            chatLVAdapter = new ChatLVAdapter(context, chatUsersVOArrayList, chatListView);
            chatListView.setAdapter(chatLVAdapter);
        }catch(Exception e){
            Log.i("MyChatFragment", "Exception has been thrown" + e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        ArrayList<ChatUsersVO> chatUsersVOArrayList = chatUsersDAO.getMyChatList(myId);

        ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        for( int i = chatUsersVOArrayList.size() - 1; i >= 0 ; i--){
            String userId = chatUsersVOArrayList.get(i).getUserId();
            if( peopleIBolcked.contains(userId) || peopleWhoBlockedMe.contains(userId) || myId.equalsIgnoreCase(userId) ){
                chatUsersVOArrayList.remove(i);
            }
        }

        Collections.sort(chatUsersVOArrayList, new Comparator<ChatUsersVO>() {
            @Override
            public int compare(ChatUsersVO lhs, ChatUsersVO rhs) {
                if ( lhs.getTime().compareTo(rhs.getTime()) > 0 )
                    return -1;
                return 1;
            }
        });

        if (chatUsersVOArrayList.isEmpty()){
            chatListView.setVisibility(View.GONE);
            tv_emptyChat.setVisibility(View.VISIBLE);
        }else{
            chatListView.setVisibility(View.VISIBLE);
            tv_emptyChat.setVisibility(View.GONE);
        }

        chatLVAdapter = new ChatLVAdapter(context,chatUsersVOArrayList,chatListView);
        chatListView.setAdapter(chatLVAdapter);
    }
}
