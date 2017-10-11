package com.amigos.sachin.MyProfileFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amigos.sachin.DAO.InterestsDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.Firebase;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class MyInterestsFragment extends Fragment {

    String myId = "";
    SharedPreferences sp;
    Context context;

    public MyInterestsFragment() {

    }

    public static MyInterestsFragment newInstance(String param1) {
        MyInterestsFragment fragment = new MyInterestsFragment();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_interests, container, false);
        context = getActivity();
        sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId", "");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TagContainerLayout mTagContainerLayoutLifestyle = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerLifeStyle);
        TagContainerLayout mTagContainerLayoutArts = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerArts);
        TagContainerLayout mTagContainerLayoutEntertainment = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerEntertainment);
        TagContainerLayout mTagContainerLayoutBusiness = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerBusiness);
        TagContainerLayout mTagContainerLayoutSports = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerSports);
        TagContainerLayout mTagContainerLayoutMusic = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerMusic);
        TagContainerLayout mTagContainerLayoutTechnology = (TagContainerLayout) view.findViewById(R.id.tagPrefContainerTechnology);

        ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
        ArrayList<String> arts = PeevesList.getAllArtsInterests();
        ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
        ArrayList<String> business = PeevesList.getAllBusinessInterests();
        ArrayList<String> sports = PeevesList.getAllSportsInterests();
        ArrayList<String> music = PeevesList.getAllMusicInterests();
        ArrayList<String> technology = PeevesList.getAllTechnologyInterests();


        InterestsDAO interestsDAO = new InterestsDAO(context);
        // interestsDAO.clearTableData();


        createInterestTags(mTagContainerLayoutLifestyle,lifestyle, "lifestyle");
        createInterestTags(mTagContainerLayoutArts,arts, "arts");
        createInterestTags(mTagContainerLayoutEntertainment,entertainment, "entertainment");
        createInterestTags(mTagContainerLayoutBusiness,business, "business");
        createInterestTags(mTagContainerLayoutSports,sports, "sports");
        createInterestTags(mTagContainerLayoutMusic,music, "music");
        createInterestTags(mTagContainerLayoutTechnology,technology, "technology");
    }

    private void createInterestTags(final TagContainerLayout mTagContainerLayout,ArrayList<String> tags, String topic) {

        mTagContainerLayout.setRippleDuration(100);
        mTagContainerLayout.setTags(tags);
        mTagContainerLayout.setBackgroundColor(Color.WHITE);
        mTagContainerLayout.setVerticalInterval(5.0f);
        mTagContainerLayout.setHorizontalInterval(5.0f);
        mTagContainerLayout.setBorderColor(Color.WHITE);


        updateTagColors(mTagContainerLayout,tags,topic);

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
                /*tag.setTagBorderColor(Color.parseColor("#F4514E"));*/
                tag.setTagBorderColor(context.getResources().getColor(R.color.colorPrimary));
            }
            tag.setHorizontalPadding(18);
            tag.setVerticalPadding(14);
            tag.setTextSize(40.0f);
            tag.setBorderWidth(3.0f);
        }
    }
}
