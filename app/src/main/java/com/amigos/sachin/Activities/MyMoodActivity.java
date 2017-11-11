package com.amigos.sachin.Activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.MainFragments.UsersFragment;
import com.amigos.sachin.R;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class MyMoodActivity extends AppCompatActivity {

    TagContainerLayout mTagContainerLayoutMyMoodTags;
    TagContainerLayout mTagContainerLayoutAllTags;
    int myMoodsTagCount = 0;
    Context context;
    LinearLayout linearLayoutButton, layoutMoodOff, layoutMoodTags, myTagsContainerLayout, allTagsContainerLayout;
    Button saveButton;
    String myId;
    ArrayList<String> initialTags = new ArrayList<String>();
    ArrayList<String> allInterests = new ArrayList<String>();
    String mood = "0";
    int interestsFlag = 0;
    public RadioGroup radiogroup_people_interests;
    View singleLineSeperator1;
    TextView tv_moodOffText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mood);

        context = getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        Firebase.setAndroidContext(context);

        mTagContainerLayoutAllTags = (TagContainerLayout) findViewById(R.id.allTags1);
        mTagContainerLayoutMyMoodTags = (TagContainerLayout) findViewById(R.id.chosenTags1);
        radiogroup_people_interests = (RadioGroup) findViewById(R.id.radiogroup_people_interests1);
        layoutMoodOff = (LinearLayout) findViewById(R.id.layoutMoodOff);
        layoutMoodTags = (LinearLayout) findViewById(R.id.layoutMoodOff);
        myTagsContainerLayout = (LinearLayout) findViewById(R.id.myTagsContainerLayout);
        allTagsContainerLayout = (LinearLayout) findViewById(R.id.allTagsContainerLayout);
        linearLayoutButton = (LinearLayout) findViewById(R.id.linearLayout_button1);
        saveButton = (Button) findViewById(R.id.myMoodsButton1);
        singleLineSeperator1 = (View) findViewById(R.id.singleLineSeperator1);
        tv_moodOffText1 = (TextView) findViewById(R.id.tv_moodOffText1);

        mTagContainerLayoutMyMoodTags.removeAllTags();

        mTagContainerLayoutAllTags.removeAllTags();

        layoutMoodOff.setVisibility(View.VISIBLE);
        layoutMoodTags.setVisibility(View.GONE);
        myTagsContainerLayout.setVisibility(View.GONE);
        allTagsContainerLayout.setVisibility(View.GONE);

        mTagContainerLayoutAllTags.setOnDragListener(new MyMoodActivity.MyDragListener());
        mTagContainerLayoutMyMoodTags.setOnDragListener(new MyMoodActivity.MyDragListener());
        myTagsContainerLayout.setOnDragListener(new MyMoodActivity.MyDragListener());
        allTagsContainerLayout.setOnDragListener(new MyMoodActivity.MyDragListener());


        updateContainer(mTagContainerLayoutAllTags);
        mTagContainerLayoutAllTags.setBackgroundColor(Color.parseColor("#fbf2fc"));

        updateContainer(mTagContainerLayoutMyMoodTags);
        mTagContainerLayoutMyMoodTags.setBackgroundColor(Color.parseColor("#fffbef"));

        setListenerToTags(mTagContainerLayoutAllTags, 1);
        setListenerToTags(mTagContainerLayoutMyMoodTags, 2);

        initialize();
        setListeners();

        String myMoodUsageDirections = sp.getString("myMoodUsageDirections","0");
        if("0".equalsIgnoreCase(myMoodUsageDirections)){
            Intent intent = new Intent(context, MyMoodUsageDirections.class);
            startActivity(intent);
        }


    }

    public void initialize(){

        Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/moods/");
        myMoodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if ("mood".equalsIgnoreCase(snapshot.getKey())){
                        if("1".equalsIgnoreCase(snapshot.getValue().toString())){
                            mood = "1";
                            radiogroup_people_interests.check(R.id.radio_people_of_particular_interests1);

                            layoutMoodTags.setVisibility(View.VISIBLE);
                            mTagContainerLayoutMyMoodTags.setVisibility(View.VISIBLE);
                            mTagContainerLayoutAllTags.setVisibility(View.VISIBLE);
                            singleLineSeperator1.setVisibility(View.VISIBLE);
                            myTagsContainerLayout.setVisibility(View.VISIBLE);
                            allTagsContainerLayout.setVisibility(View.VISIBLE);
                            layoutMoodOff.setVisibility(View.GONE);
                            tv_moodOffText1.setVisibility(View.GONE);

                        }else{
                            mood = "0";
                            radiogroup_people_interests.check(R.id.radio_people_of_my_interests1);

                            layoutMoodTags.setVisibility(View.GONE);
                            mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
                            mTagContainerLayoutAllTags.setVisibility(View.GONE);
                            singleLineSeperator1.setVisibility(View.GONE);
                            myTagsContainerLayout.setVisibility(View.GONE);
                            allTagsContainerLayout.setVisibility(View.GONE);
                            layoutMoodOff.setVisibility(View.VISIBLE);
                            tv_moodOffText1.setVisibility(View.VISIBLE);
                        }
                    }
                    if("interests".equalsIgnoreCase(snapshot.getKey())){
                        initialTags.clear();
                        for(DataSnapshot snap : snapshot.getChildren()){
                            initialTags.add(snap.getKey());
                        }
                        mTagContainerLayoutMyMoodTags.removeAllTags();
                        mTagContainerLayoutMyMoodTags.setTags(initialTags);

                        ArrayList<String> allTopics = PeevesList.getNewInterestsTopics();
                        for(String s: initialTags){
                            allTopics.remove(s);
                        }
                        allInterests.clear();
                        allInterests.addAll(allTopics);
                        mTagContainerLayoutAllTags.setTags(allInterests);

                        updateContainer(mTagContainerLayoutAllTags);
                        updateContainer(mTagContainerLayoutMyMoodTags);

                        setListenerToTags(mTagContainerLayoutMyMoodTags, 1);
                        setListenerToTags(mTagContainerLayoutAllTags, 2);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setListeners() {

        radiogroup_people_interests.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio_people_of_my_interests1){
                    mood = "0";
                    Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                    Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/moods/");
                    myMoodsRef.child("mood").setValue("0");

                    layoutMoodTags.setVisibility(View.GONE);
                    mTagContainerLayoutMyMoodTags.setVisibility(View.GONE);
                    mTagContainerLayoutAllTags.setVisibility(View.GONE);
                    singleLineSeperator1.setVisibility(View.GONE);
                    myTagsContainerLayout.setVisibility(View.GONE);
                    allTagsContainerLayout.setVisibility(View.GONE);
                    layoutMoodOff.setVisibility(View.VISIBLE);
                    tv_moodOffText1.setVisibility(View.VISIBLE);

                }else if (checkedId == R.id.radio_people_of_particular_interests1) {
                    mood = "1";
                    Firebase moodsRef = new Firebase("https://new-amigos.firebaseio.com/moods/");
                    Firebase myMoodsRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/moods/");
                    myMoodsRef.child("mood").setValue("1");

                    layoutMoodTags.setVisibility(View.VISIBLE);
                    mTagContainerLayoutMyMoodTags.setVisibility(View.VISIBLE);
                    mTagContainerLayoutAllTags.setVisibility(View.VISIBLE);
                    singleLineSeperator1.setVisibility(View.VISIBLE);
                    myTagsContainerLayout.setVisibility(View.VISIBLE);
                    allTagsContainerLayout.setVisibility(View.VISIBLE);
                    layoutMoodOff.setVisibility(View.GONE);
                    tv_moodOffText1.setVisibility(View.GONE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(radiogroup_people_interests.getCheckedRadioButtonId() == R.id.radio_people_of_my_interests1){

                    Toast.makeText(context, "Now, you will see people on the basis of your interests", Toast.LENGTH_SHORT).show();
                    reloadData();
                    onBackPressed();

                }else {

                    ArrayList<String> myTags = new ArrayList<String>();
                    myTags = (ArrayList<String>) mTagContainerLayoutMyMoodTags.getTags();
                    if (myTags.isEmpty()) {
                        Toast.makeText(context, "Choose atleast one tag.", Toast.LENGTH_SHORT).show();
                    } else if (myTags.size() > 5 ) {
                        Toast.makeText(context, "Choose only upto 5 tags", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(context, "Data saved.", Toast.LENGTH_LONG).show();
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
                        reloadData();
                        onBackPressed();
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class MyDragListener implements View.OnDragListener {


        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    if (v instanceof TagContainerLayout) {
                        View view = (View) event.getLocalState();
                        ViewGroup owner = (ViewGroup) view.getParent();
                        //owner.removeView(view);

                        if((((TagContainerLayout)owner)).getId() == R.id.allTags1 && mTagContainerLayoutMyMoodTags.getTags().size() >=5 ){
                            Toast.makeText(context, "You can add upto 5 tags only!!!",Toast.LENGTH_SHORT).show();
                        }else {
                            List<String> list = ((TagContainerLayout) owner).getTags();
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).equalsIgnoreCase(((TagView) view).getText())) {
                                    ((TagContainerLayout) owner).removeTag(j);
                                }
                            }

                            TagContainerLayout container = (TagContainerLayout) v;
                            container.addTag(((TagView) view).getText(), 0);
                            TagView tag = container.getTagView(0);
                            if (container.getId() == R.id.chosenTags1) {
                                setListenerToTags(container, 1);
                            } else if (container.getId() == R.id.allTags1) {
                                setListenerToTags(container, 2);
                            }
                        }
                    }else if(v instanceof LinearLayout){
                        int childCount = ((LinearLayout) v).getChildCount();
                        for(int i = 0 ; i<childCount; i++){
                            View v1 = ((LinearLayout) v).getChildAt(i);
                            if(v1 instanceof TagContainerLayout){
                                View view = (View) event.getLocalState();
                                ViewGroup owner = (ViewGroup) view.getParent();
                                //owner.removeView(view);
                                if((((TagContainerLayout)owner)).getId() == R.id.allTags1 && mTagContainerLayoutMyMoodTags.getTags().size() >= 5 ){
                                    Toast.makeText(context, "You can add upto 5 tags only!!!",Toast.LENGTH_SHORT).show();
                                }else {

                                    List<String> list = ((TagContainerLayout) owner).getTags();
                                    for (int j = 0; j < list.size(); j++) {
                                        if (list.get(j).equalsIgnoreCase(((TagView) view).getText())) {
                                            ((TagContainerLayout) owner).removeTag(j);
                                        }
                                    }
                                    TagContainerLayout container = (TagContainerLayout) v1;
                                    container.addTag(((TagView) view).getText(), 0);
                                /*view.setVisibility(View.VISIBLE);*/
                                    TagView tag = container.getTagView(0);
                                    if (container.getId() == R.id.chosenTags1) {
                                        setListenerToTags(container, 1);
                                    } else if (container.getId() == R.id.allTags1) {
                                        setListenerToTags(container, 2);
                                    }
                                }

                                //updateContainer(container);
                            }
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private void setListenerToTags(TagContainerLayout tags, int position ) {
        for (int i = 0; i<tags.getTags().size(); i++){
            TagView tag = tags.getTagView(i);
            tag.setBorderRadius(25.0f);
            tag.setOnTouchListener(new MyMoodActivity.MyTouchListener());
            if(position == 1){
                tag.setTagBackgroundColor(Color.parseColor("#7986CB"));
                tag.setTagTextColor(Color.WHITE);//Blue
                tag.setTagBorderColor(Color.WHITE);
            }else if(position == 2){
                tag.setTagBackgroundColor(Color.parseColor("#FFD54F"));
                tag.setTagTextColor(Color.WHITE);//Blue
                tag.setTagBorderColor(Color.WHITE);
            }
        }
    }

    private void updateContainer(TagContainerLayout mTagContainerLayoutAllTags) {
        mTagContainerLayoutAllTags.setRippleDuration(100);
        mTagContainerLayoutAllTags.setVerticalInterval(3.0f);
        mTagContainerLayoutAllTags.setHorizontalInterval(3.0f);
        mTagContainerLayoutAllTags.setBorderColor(Color.TRANSPARENT);
    }

    public static void reloadData(){
        new LongOperation().execute("");
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
            /*if (flag == 1) {
                TabLayout tabLayout;
                tabLayout = (TabLayout) fragmentContext.findViewById(R.id.tabs);
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                tab.select();
                flag = 0;
            }*/
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
