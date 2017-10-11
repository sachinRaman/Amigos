package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.amigos.sachin.Adapters.ChatLVAdapter;
import com.amigos.sachin.Adapters.LikedUsersLVAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;
import com.amigos.sachin.VO.UserVO;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    EditText et_search_people;
    ListView listView_search_people;
    String myId;
    Context context;
    static LikedUsersLVAdapter searchPeopleLVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = getApplicationContext();

        et_search_people = (EditText) findViewById(R.id.et_search_people);
        listView_search_people = (ListView) findViewById(R.id.listView_search_people);
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        final ArrayList<UserVO> userVOArrayList = ApplicationCache.userVOArrayList;
        final ArrayList<String> peopleIBolcked = ApplicationCache.peopleIBlockedList;
        final ArrayList<String> peopleWhoBlockedMe = ApplicationCache.peopleWhoBlockedMeList;

        et_search_people.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<LikedUserVO> peopleISearchedVOArrayList = new ArrayList<LikedUserVO>();
                if(s.toString().isEmpty()){
                    peopleISearchedVOArrayList.clear();
                }else{
                    peopleISearchedVOArrayList.clear();
                    for( int i = 0; i < userVOArrayList.size() ; i++){
                        String userId = userVOArrayList.get(i).getId();
                        if( !peopleIBolcked.contains(userId) && !peopleWhoBlockedMe.contains(userId) && !myId.equalsIgnoreCase(userId) ){
                            if(userVOArrayList.get(i).getName() != null) {
                                if (userVOArrayList.get(i).getName().toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
                                    LikedUserVO searchedUserVO = new LikedUserVO();
                                    searchedUserVO.setName(userVOArrayList.get(i).getName());
                                    searchedUserVO.setId(userId);
                                    searchedUserVO.setStatus(userVOArrayList.get(i).getStatus());
                                    searchedUserVO.setTime("");
                                    searchedUserVO.setImageUrl(userVOArrayList.get(i).getImageUrl());
                                    peopleISearchedVOArrayList.add(searchedUserVO);
                                }
                            }
                        }
                    }

                }

                searchPeopleLVAdapter = new LikedUsersLVAdapter(context,peopleISearchedVOArrayList,listView_search_people);
                listView_search_people.setAdapter(searchPeopleLVAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }
}
