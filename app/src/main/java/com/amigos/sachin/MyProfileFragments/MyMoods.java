package com.amigos.sachin.MyProfileFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.MainFragments.UsersFragment;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.UserVO;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;*/

//import com.firebase.client.DataSnapshot;


public class MyMoods extends Fragment {

    TagContainerLayout mTagContainerLayoutMyMoodTags;
    TagContainerLayout mTagContainerLayoutAllTags;
    int myMoodsTagCount = 0;
    Context context;
    EditText  et_search;
    TextView tv_moodOffText, tv_loadMore;

    View tv_allTags;
    LinearLayout linearLayoutButton;
    Button saveButton;
    View viewBlankLine, tv_addedTags;
    String myId;
    ArrayList<String> initialTags = new ArrayList<String>();
    ArrayList<String> allInterests = new ArrayList<String>();
    ArrayList<String> shortList = new ArrayList<String>();
    String mood = "0";
    int interestsFlag = 0;
    UserVO myVO;
    public RadioGroup radiogroup_people_interests;
    public static FragmentActivity fragmentContext;
    static int flag = 0;

    public MyMoods() {

    }

    public static MyMoods newInstance(String param1) {
        MyMoods fragment = new MyMoods();
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

        View view =  inflater.inflate(R.layout.fragment_my_moods, container, false);
        context = getActivity();
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        fragmentContext = getActivity();



        mTagContainerLayoutAllTags = (TagContainerLayout) view.findViewById(R.id.allTags);
        mTagContainerLayoutMyMoodTags = (TagContainerLayout) view.findViewById(R.id.chosenTags);
        tv_addedTags = (View) view.findViewById(R.id.tv_addedTags);
        tv_allTags = (View) view.findViewById(R.id.tv_allTags);
        radiogroup_people_interests = (RadioGroup) view.findViewById(R.id.radiogroup_people_interests);
        linearLayoutButton = (LinearLayout) view.findViewById(R.id.linearLayout_button);
        saveButton = (Button) view.findViewById(R.id.myMoodsButton);
        et_search = (EditText) view.findViewById(R.id.et_search);
        viewBlankLine = (View) view.findViewById(R.id.viewBlankLine);
        tv_moodOffText = (TextView) view.findViewById(R.id.tv_moodOffText);
        tv_loadMore = (TextView) view.findViewById(R.id.tv_loadMore);

        mTagContainerLayoutMyMoodTags.removeAllTags();
        mTagContainerLayoutAllTags.removeAllTags();

        tv_moodOffText.setVisibility(View.VISIBLE);

        tv_addedTags.setVisibility(View.GONE);
        tv_allTags.setVisibility(View.GONE);
        mTagContainerLayoutAllTags.setVisibility(View.GONE);
        mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
        et_search.setVisibility(View.GONE);
        viewBlankLine.setVisibility(View.GONE);
        tv_loadMore.setVisibility(View.GONE);

        initialize();
        updateAllInterests();
        setListeners();

        return view;
    }

    public void initialize(){

        myVO =  ApplicationCache.myUserVO;
        if("1".equalsIgnoreCase(myVO.getMood())){
            mood = "1";
            radiogroup_people_interests.check(R.id.radio_people_of_particular_interests);
            tv_addedTags.setVisibility(View.VISIBLE);
            tv_allTags.setVisibility(View.VISIBLE);
            mTagContainerLayoutAllTags.setVisibility(View.VISIBLE);
            mTagContainerLayoutMyMoodTags.setVisibility(View.VISIBLE);
            et_search.setVisibility(View.VISIBLE);
            viewBlankLine.setVisibility(View.VISIBLE);
            tv_loadMore.setVisibility(View.VISIBLE);
            tv_moodOffText.setVisibility(View.GONE);
        }else{
            mood = "0";
            radiogroup_people_interests.check(R.id.radio_people_of_my_interests);
            //myMoodsSwitch.setImageDrawable(res1);
            //et_myMood.setVisibility(View.GONE);
            tv_addedTags.setVisibility(View.GONE);
            tv_allTags.setVisibility(View.GONE);
            mTagContainerLayoutAllTags.setVisibility(View.GONE);
            mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
            et_search.setVisibility(View.GONE);
            //tv_moodTextBox.setVisibility(View.GONE);
            viewBlankLine.setVisibility(View.GONE);
            tv_loadMore.setVisibility(View.GONE);
            tv_moodOffText.setVisibility(View.VISIBLE);
        }
        if(myVO.getMoodTopic() != null){
            //et_myMood.setText(myVO.getMoodTopic());
        }
        if(myVO.getMyMoodTags() != null){
            initialTags = ApplicationCache.myUserVO.getMyMoodTags();
            mTagContainerLayoutMyMoodTags.removeAllTags();
            mTagContainerLayoutMyMoodTags.setTags(initialTags);
            myMoodsTagCount = initialTags.size();
            for (int i = 0; i<initialTags.size(); i++){
                TagView tag = mTagContainerLayoutMyMoodTags.getTagView(i);
                tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    private void setListeners() {

        radiogroup_people_interests.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio_people_of_my_interests){
                    mood = "0";
                    ApplicationCache.myUserVO.setMood("0");
                    Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                    Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/moods/");
                    myMoodsRef.child("mood").setValue("0");
                    tv_addedTags.setVisibility(View.GONE);
                    tv_allTags.setVisibility(View.GONE);
                    mTagContainerLayoutAllTags.setVisibility(View.GONE);
                    mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
                    et_search.setVisibility(View.GONE);
                    viewBlankLine.setVisibility(View.GONE);
                    tv_loadMore.setVisibility(View.GONE);
                    tv_moodOffText.setVisibility(View.VISIBLE);
                }else if (checkedId == R.id.radio_people_of_particular_interests) {
                    mood = "1";
                    ApplicationCache.myUserVO.setMood("1");
                    Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                    Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/moods/");
                    myMoodsRef.child("mood").setValue("1");
                    tv_addedTags.setVisibility(View.VISIBLE);
                    tv_allTags.setVisibility(View.VISIBLE);
                    mTagContainerLayoutAllTags.setVisibility(View.VISIBLE);
                    mTagContainerLayoutMyMoodTags.setVisibility(View.VISIBLE);
                    et_search.setVisibility(View.VISIBLE);
                    viewBlankLine.setVisibility(View.VISIBLE);
                    tv_loadMore.setVisibility(View.VISIBLE);
                    tv_moodOffText.setVisibility(View.GONE);
                }
            }
        });


        mTagContainerLayoutAllTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

                List<String> myTags = new ArrayList<String>();
                myTags = mTagContainerLayoutMyMoodTags.getTags();
                if(myTags.contains(text)){
                    final Toast toast1 =  Toast.makeText(context,"Tag already added.",Toast.LENGTH_SHORT);
                    toast1.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast1.cancel();
                        }
                    }, 1000);
                }else {
                    mTagContainerLayoutMyMoodTags.addTag(text);
                    TagView tag = mTagContainerLayoutMyMoodTags.getTagView(myMoodsTagCount);
                    tag.setTagBackgroundColor(Color.WHITE);
                    tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                    tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
                    myMoodsTagCount++;
                    et_search.setText("");
                    final Toast toast = Toast.makeText(context,text + " added.",Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);
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

                flag = 1;

                if(radiogroup_people_interests.getCheckedRadioButtonId() == R.id.radio_people_of_my_interests){

                    Toast.makeText(context, "Now, you will see people on the basis of your interests", Toast.LENGTH_SHORT).show();
                    reloadData();

                }else {

                    ArrayList<String> myTags = new ArrayList<String>();
                    myTags = (ArrayList<String>) mTagContainerLayoutMyMoodTags.getTags();
                    if (myTags.isEmpty()) {
                        Toast.makeText(context, "Choose atleast one tag", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(context, "Now, you will see people on the basis of the topics you have chosen", Toast.LENGTH_SHORT).show();
                        ApplicationCache.myUserVO.setMyMoodTags(myTags);
                        reloadData();
                        Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                        Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/moods/");
                        for (String s1 : initialTags) {
                            moodsRef.child(s1).child(myId).setValue(null);
                            myMoodsRef.child("interests").child(s1).setValue(null);
                        }
                        for (String s : myTags) {
                            moodsRef.child(s).child(myId).setValue("1");
                            myMoodsRef.child("interests").child(s).setValue("1");
                        }

                    }
                }


            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    if(interestsFlag == 1) {
                        mTagContainerLayoutAllTags.removeAllTags();
                        mTagContainerLayoutAllTags.setTags(allInterests);
                        for (int i = 0; i < allInterests.size(); i++) {
                            TagView tag = mTagContainerLayoutAllTags.getTagView(i);
                            tag.setTagBackgroundColor(Color.WHITE);
                            tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                            tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
                        }
                    }else{
                        mTagContainerLayoutAllTags.removeAllTags();
                        mTagContainerLayoutAllTags.setTags(shortList);
                        for (int i = 0; i < shortList.size(); i++) {
                            TagView tag = mTagContainerLayoutAllTags.getTagView(i);
                            tag.setTagBackgroundColor(Color.WHITE);
                            tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                            tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
                        }
                    }
                }else {
                    ArrayList<String> searchArrayList = new ArrayList<String>();
                    Set<String> set = new HashSet<String>();
                    for (String s1 : allInterests) {
                        if (s1.toLowerCase().contains(s.toString().toLowerCase())) {
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
                        tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                        tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interestsFlag == 0){
                    interestsFlag = 1;
                    tv_loadMore.setText("Click to load less tags...");
                    mTagContainerLayoutAllTags.removeAllTags();
                    mTagContainerLayoutAllTags.setTags(allInterests);
                    for (int i = 0; i < allInterests.size(); i++) {
                        TagView tag = mTagContainerLayoutAllTags.getTagView(i);
                        tag.setTagBackgroundColor(Color.WHITE);
                        tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                        tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                }else{
                    interestsFlag = 0;
                    tv_loadMore.setText("Click to load more tags...");
                    mTagContainerLayoutAllTags.removeAllTags();
                    mTagContainerLayoutAllTags.setTags(shortList);
                    for (int i = 0; i < shortList.size(); i++) {
                        TagView tag = mTagContainerLayoutAllTags.getTagView(i);
                        tag.setTagBackgroundColor(Color.WHITE);
                        tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                        tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                }
            }
        });
    }

    public static void reloadData(){
        new LongOperation().execute("");
    }

    public void updateAllInterests(){
        /*ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
        ArrayList<String> arts = PeevesList.getAllArtsInterests();
        ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
        ArrayList<String> business = PeevesList.getAllBusinessInterests();
        ArrayList<String> sports = PeevesList.getAllSportsInterests();
        ArrayList<String> music = PeevesList.getAllMusicInterests();
        ArrayList<String> technology = PeevesList.getAllTechnologyInterests();*/
        ArrayList<String> allTopics = PeevesList.getNewInterestsTopics();
        shortList.clear();
        for(int i = 0; i<20; i++){
            shortList.add(allTopics.get(i));
        }

        allInterests.clear();
        allInterests.addAll(allTopics);
        /*allInterests.addAll(lifestyle);
        allInterests.addAll(arts);
        allInterests.addAll(entertainment);
        allInterests.addAll(business);
        allInterests.addAll(sports);
        allInterests.addAll(music);
        allInterests.addAll(technology);*/

        mTagContainerLayoutAllTags.setRippleDuration(100);
        mTagContainerLayoutAllTags.setTags(shortList);
        mTagContainerLayoutAllTags.setBackgroundColor(Color.WHITE);
        mTagContainerLayoutAllTags.setVerticalInterval(5.0f);
        mTagContainerLayoutAllTags.setHorizontalInterval(5.0f);
        mTagContainerLayoutAllTags.setBorderColor(Color.WHITE);

        mTagContainerLayoutMyMoodTags.setRippleDuration(100);
        mTagContainerLayoutMyMoodTags.setBackgroundColor(Color.WHITE);
        mTagContainerLayoutMyMoodTags.setVerticalInterval(5.0f);
        mTagContainerLayoutMyMoodTags.setHorizontalInterval(5.0f);
        mTagContainerLayoutMyMoodTags.setBorderColor(Color.WHITE);

        for (int i = 0; i<shortList.size(); i++){
            TagView tag = mTagContainerLayoutAllTags.getTagView(i);
            tag.setTagBackgroundColor(Color.WHITE);
            tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
            tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }


    private static class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String

            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            UsersFragment usersFragment = new UsersFragment();
            usersFragment.reloadAllUsers();
            if (flag == 1) {
                TabLayout tabLayout;
                tabLayout = (TabLayout) fragmentContext.findViewById(R.id.tabs);
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                tab.select();
                flag = 0;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
