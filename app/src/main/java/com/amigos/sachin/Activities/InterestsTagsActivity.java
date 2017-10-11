package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amigos.sachin.DAO.InterestsDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.Firebase;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class InterestsTagsActivity extends AppCompatActivity {

    String myId = "";
    SharedPreferences sp;
    Context context;
    Button interestsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests_tags);

        context = getApplicationContext();
        sp = getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId", "");

        TagContainerLayout mTagContainerLayoutLifestyle = (TagContainerLayout) findViewById(R.id.tagPrefContainerLifeStyle1);
        TagContainerLayout mTagContainerLayoutArts = (TagContainerLayout) findViewById(R.id.tagPrefContainerArts1);
        TagContainerLayout mTagContainerLayoutEntertainment = (TagContainerLayout) findViewById(R.id.tagPrefContainerEntertainment1);
        TagContainerLayout mTagContainerLayoutBusiness = (TagContainerLayout) findViewById(R.id.tagPrefContainerBusiness1);
        TagContainerLayout mTagContainerLayoutSports = (TagContainerLayout) findViewById(R.id.tagPrefContainerSports1);
        TagContainerLayout mTagContainerLayoutMusic = (TagContainerLayout) findViewById(R.id.tagPrefContainerMusic1);
        TagContainerLayout mTagContainerLayoutTechnology = (TagContainerLayout) findViewById(R.id.tagPrefContainerTechnology1);
        interestsButton = (Button) findViewById(R.id.buttonInterests);

        ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
        ArrayList<String> arts = PeevesList.getAllArtsInterests();
        ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
        ArrayList<String> business = PeevesList.getAllBusinessInterests();
        ArrayList<String> sports = PeevesList.getAllSportsInterests();
        ArrayList<String> music = PeevesList.getAllMusicInterests();
        ArrayList<String> technology = PeevesList.getAllTechnologyInterests();

        createInterestTags(mTagContainerLayoutLifestyle,lifestyle, "lifestyle");
        createInterestTags(mTagContainerLayoutArts,arts, "arts");
        createInterestTags(mTagContainerLayoutEntertainment,entertainment, "entertainment");
        createInterestTags(mTagContainerLayoutBusiness,business, "business");
        createInterestTags(mTagContainerLayoutSports,sports, "sports");
        createInterestTags(mTagContainerLayoutMusic,music, "music");
        createInterestTags(mTagContainerLayoutTechnology,technology, "technology");

        interestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterestsTagsActivity.this,MyMoodActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createInterestTags(final TagContainerLayout mTagContainerLayout,ArrayList<String> tags, String topic) {

        mTagContainerLayout.setRippleDuration(100);
        mTagContainerLayout.setTags(tags);
        mTagContainerLayout.setBackgroundColor(Color.WHITE);
        mTagContainerLayout.setVerticalInterval(5.0f);
        mTagContainerLayout.setHorizontalInterval(5.0f);
        mTagContainerLayout.setBorderColor(Color.WHITE);


        updateTagColors(mTagContainerLayout,tags,topic);

        Firebase.setAndroidContext(context);

        final InterestsDAO interestsDAO = new InterestsDAO(context);
        final Firebase usersInterestsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/interests_list/"+topic+"/");

        mTagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagView tag = mTagContainerLayout.getTagView(position);
                if(interestsDAO.getInterestPref(text) == 0){

                    usersInterestsRef.child(tag.getText()).setValue("1");
                    interestsDAO.changeInterest(text,1);
                    tag.setTagBackgroundColor(Color.WHITE);
                    tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                    tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));

                }else if(interestsDAO.getInterestPref(text) == 1){

                    usersInterestsRef.child(tag.getText()).setValue("0");
                    interestsDAO.changeInterest(text,0);
                    tag.setTagBackgroundColor(Color.WHITE);
                    tag.setTagTextColor(Color.GRAY);
                    tag.setTagBorderColor(Color.GRAY);

                }

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    public void updateTagColors(TagContainerLayout mTagContainerLayout, ArrayList<String> list,String topic){
        InterestsDAO interestsDAO = new InterestsDAO(context);

        int size = list.size();
        for (int i = 0; i<size; i++){
            TagView tag = mTagContainerLayout.getTagView(i);
            if (interestsDAO.getInterestPref(tag.getText()) == 0){
                tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.GRAY);
                tag.setTagBorderColor(Color.GRAY);
            }else if(interestsDAO.getInterestPref(tag.getText()) == 1){
                tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(context.getResources().getColor(R.color.colorPrimary));
                tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
            }
            tag.setHorizontalPadding(18);
            tag.setVerticalPadding(14);
            tag.setTextSize(40.0f);
            tag.setBorderWidth(3.0f);
        }
    }
}
