package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.R;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class MyMoodActivity extends AppCompatActivity {

    TagContainerLayout mTagContainerLayoutMyMoodTags;
    TagContainerLayout mTagContainerLayoutAllTags;
    Switch myMoodsSwitch;
    int myMoodsTagCount = 0;
    Context context;
    EditText et_myMood, et_search;
    TextView tv_addedTags,tv_allTags;
    LinearLayout linearLayoutButton;
    Button saveButton;
    String myId;
    ArrayList<String> initialTags = new ArrayList<String>();
    ArrayList<String> allInterests = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mood);

        context = getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        mTagContainerLayoutAllTags = (TagContainerLayout) findViewById(R.id.allTags1);
        mTagContainerLayoutMyMoodTags = (TagContainerLayout) findViewById(R.id.chosenTags1);
        myMoodsSwitch = (Switch) findViewById(R.id.switch11);
        et_myMood = (EditText) findViewById(R.id.et_myMood1);
        tv_addedTags = (TextView) findViewById(R.id.tv_addedTags1);
        tv_allTags = (TextView) findViewById(R.id.tv_allTags1);
        linearLayoutButton = (LinearLayout) findViewById(R.id.linearLayout_button1);
        saveButton = (Button) findViewById(R.id.myMoodsButton1);
        et_search = (EditText) findViewById(R.id.et_search1);

        if(!myMoodsSwitch.isChecked()){
            et_myMood.setVisibility(View.GONE);
            tv_addedTags.setVisibility(View.GONE);
            tv_allTags.setVisibility(View.GONE);
            mTagContainerLayoutAllTags.setVisibility(View.GONE);
            mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
            et_search.setVisibility(View.GONE);
        }
        initialize();
        updateAllInterests();
        setListeners();
    }

    public void initialize(){
        Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/moods/");
        myMoodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if("mood".equalsIgnoreCase(snapshot.getKey())){
                        if("1".equalsIgnoreCase(snapshot.getValue().toString())){
                            myMoodsSwitch.setChecked(true);
                            et_myMood.setVisibility(View.VISIBLE);
                            tv_addedTags.setVisibility(View.VISIBLE);
                            tv_allTags.setVisibility(View.VISIBLE);
                            mTagContainerLayoutAllTags.setVisibility(View.VISIBLE);
                            mTagContainerLayoutMyMoodTags.setVisibility(View.VISIBLE);
                            et_search.setVisibility(View.VISIBLE);
                        }else{
                            myMoodsSwitch.setChecked(false);
                            et_myMood.setVisibility(View.GONE);
                            tv_addedTags.setVisibility(View.GONE);
                            tv_allTags.setVisibility(View.GONE);
                            mTagContainerLayoutAllTags.setVisibility(View.GONE);
                            mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
                            et_search.setVisibility(View.GONE);
                        }
                    }
                    if("topic".equalsIgnoreCase(snapshot.getKey())){
                        et_myMood.setText(snapshot.getValue().toString());
                    }
                    if("interests".equalsIgnoreCase(snapshot.getKey())){
                        for (DataSnapshot data : snapshot.getChildren()){
                            initialTags.add(data.getKey().toString());
                        }
                        mTagContainerLayoutMyMoodTags.setTags(initialTags);
                        myMoodsTagCount = initialTags.size();
                        for (int i = 0; i<initialTags.size(); i++){
                            TagView tag = mTagContainerLayoutMyMoodTags.getTagView(i);
                            tag.setTagBackgroundColor(Color.WHITE);
                            tag.setTagTextColor(Color.parseColor("#F4514E"));
                            tag.setTagBorderColor(Color.parseColor("#F4514E"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setListeners() {
        myMoodsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/moods/");
                if(!myMoodsSwitch.isChecked()){
                    myMoodsRef.child("mood").setValue("0");
                    et_myMood.setVisibility(View.GONE);
                    tv_addedTags.setVisibility(View.GONE);
                    tv_allTags.setVisibility(View.GONE);
                    mTagContainerLayoutAllTags.setVisibility(View.GONE);
                    mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
                    et_search.setVisibility(View.GONE);
                }else{
                    myMoodsRef.child("mood").setValue("1");
                    et_myMood.setVisibility(View.VISIBLE);
                    tv_addedTags.setVisibility(View.VISIBLE);
                    tv_allTags.setVisibility(View.VISIBLE);
                    mTagContainerLayoutAllTags.setVisibility(View.VISIBLE);
                    mTagContainerLayoutMyMoodTags.setVisibility(View.VISIBLE);
                    et_search.setVisibility(View.VISIBLE);
                }
            }
        });


        mTagContainerLayoutAllTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                if (myMoodsTagCount < 5){
                    List<String> myTags = new ArrayList<String>();
                    myTags = mTagContainerLayoutMyMoodTags.getTags();
                    if(myTags.contains(text)){
                        Toast.makeText(context,"Tag already added.",Toast.LENGTH_LONG).show();
                    }else {
                        mTagContainerLayoutMyMoodTags.addTag(text);
                        TagView tag = mTagContainerLayoutMyMoodTags.getTagView(myMoodsTagCount);
                        tag.setTagBackgroundColor(Color.WHITE);
                        tag.setTagTextColor(Color.parseColor("#F4514E"));
                        tag.setTagBorderColor(Color.parseColor("#F4514E"));
                        myMoodsTagCount++;
                        Toast.makeText(context,text + " added.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context,"You can choose upto 5 tags only",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
            }
        });

        mTagContainerLayoutMyMoodTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                myMoodsTagCount--;
                mTagContainerLayoutMyMoodTags.removeTag(position);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myMoodsSwitch.isChecked()) {
                    if (et_myMood.getText() == null || et_myMood.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Mention what's on your mind.", Toast.LENGTH_SHORT).show();
                    } else {
                        List<String> myTags = new ArrayList<String>();
                        myTags = mTagContainerLayoutMyMoodTags.getTags();
                        if (myTags.isEmpty()) {
                            Toast.makeText(context, "Choose atleast one tag.", Toast.LENGTH_SHORT).show();
                        } else {
                            Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                            Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/moods/");
                            for (String s1 : initialTags) {
                                moodsRef.child(s1).child(myId).setValue(null);
                                myMoodsRef.child("interests").child(s1).setValue(null);
                            }
                            for (String s : myTags) {
                                moodsRef.child(s).child(myId).setValue("1");
                                myMoodsRef.child("topic").setValue(et_myMood.getText().toString().trim());
                                myMoodsRef.child("interests").child(s).setValue("1");
                            }
                            Toast.makeText(context, "Data saved.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                Intent intent = new Intent(MyMoodActivity.this,SplashScreen2.class);
                startActivity(intent);
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    mTagContainerLayoutAllTags.removeAllTags();
                    mTagContainerLayoutAllTags.setTags(allInterests);
                    for (int i = 0; i<allInterests.size(); i++){
                        TagView tag = mTagContainerLayoutAllTags.getTagView(i);
                        tag.setTagBackgroundColor(Color.WHITE);
                        tag.setTagTextColor(Color.parseColor("#F4514E"));
                        tag.setTagBorderColor(Color.parseColor("#F4514E"));
                    }
                }else {
                    ArrayList<String> searchArrayList = new ArrayList<String>();
                    Set<String> set = new HashSet<String>();
                    for (String s1 : allInterests) {
                        if (s1.startsWith(s.toString())) {
                            set.add(s1);
                        }
                    }
                    mTagContainerLayoutAllTags.removeAllTags();
                    searchArrayList.clear();
                    searchArrayList.addAll(set);
                    mTagContainerLayoutAllTags.setTags(searchArrayList);
                    for (int i = 0; i < searchArrayList.size(); i++) {
                        TagView tag = mTagContainerLayoutAllTags.getTagView(i);
                        tag.setTagBackgroundColor(Color.WHITE);
                        tag.setTagTextColor(Color.parseColor("#F4514E"));
                        tag.setTagBorderColor(Color.parseColor("#F4514E"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void updateAllInterests(){
        ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
        ArrayList<String> arts = PeevesList.getAllArtsInterests();
        ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
        ArrayList<String> business = PeevesList.getAllBusinessInterests();
        ArrayList<String> sports = PeevesList.getAllSportsInterests();
        ArrayList<String> music = PeevesList.getAllMusicInterests();
        ArrayList<String> technology = PeevesList.getAllTechnologyInterests();

        allInterests.addAll(lifestyle);
        allInterests.addAll(arts);
        allInterests.addAll(entertainment);
        allInterests.addAll(business);
        allInterests.addAll(sports);
        allInterests.addAll(music);
        allInterests.addAll(technology);

        mTagContainerLayoutAllTags.setRippleDuration(100);
        mTagContainerLayoutAllTags.setTags(allInterests);
        mTagContainerLayoutAllTags.setBackgroundColor(Color.WHITE);
        mTagContainerLayoutAllTags.setVerticalInterval(5.0f);
        mTagContainerLayoutAllTags.setHorizontalInterval(5.0f);
        mTagContainerLayoutAllTags.setBorderColor(Color.WHITE);

        mTagContainerLayoutMyMoodTags.setRippleDuration(100);
        mTagContainerLayoutMyMoodTags.setBackgroundColor(Color.WHITE);
        mTagContainerLayoutMyMoodTags.setVerticalInterval(5.0f);
        mTagContainerLayoutMyMoodTags.setHorizontalInterval(5.0f);
        mTagContainerLayoutMyMoodTags.setBorderColor(Color.WHITE);

        for (int i = 0; i<allInterests.size(); i++){
            TagView tag = mTagContainerLayoutAllTags.getTagView(i);
            tag.setTagBackgroundColor(Color.WHITE);
            tag.setTagTextColor(Color.parseColor("#F4514E"));
            tag.setTagBorderColor(Color.parseColor("#F4514E"));
        }
    }
}
