package com.amigos.sachin.MyProfileFragments;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.amigos.sachin.Activities.DragActivity;
import com.amigos.sachin.Activities.MyInterestsUsageDirections;
import com.amigos.sachin.DAO.InterestsDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class MyInterestsFragment extends Fragment {

    //LinearLayout layout_tags1, layout_tags2, layout_tags3, layout_tags4;
    TagContainerLayout tags1, tags2, tags3, tags4;
    Context context;
    ScrollView scroll_tag1, scroll_tag2, scroll_tag3, scroll_tag4;
    Button saveButton;
    String myId;
    ArrayList<String> interestsList1;
    int position = 0;
    SharedPreferences sp;

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
        sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        Firebase.setAndroidContext(context);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tags1 = (TagContainerLayout) view.findViewById(R.id.tags1);
        tags2 = (TagContainerLayout) view.findViewById(R.id.tags2);
        tags3 = (TagContainerLayout) view.findViewById(R.id.tags3);
        tags4 = (TagContainerLayout) view.findViewById(R.id.tags4);

        scroll_tag1 = (ScrollView) view.findViewById(R.id.scroll_tag1);
        scroll_tag2 = (ScrollView) view.findViewById(R.id.scroll_tag2);
        scroll_tag3 = (ScrollView) view.findViewById(R.id.scroll_tag3);
        scroll_tag4 = (ScrollView) view.findViewById(R.id.scroll_tag4);

        saveButton = (Button) view.findViewById(R.id.saveInterests);

        scroll_tag1.setOnDragListener(new MyInterestsFragment.MyDragListener());
        scroll_tag2.setOnDragListener(new MyInterestsFragment.MyDragListener());
        scroll_tag3.setOnDragListener(new MyInterestsFragment.MyDragListener());
        scroll_tag4.setOnDragListener(new MyInterestsFragment.MyDragListener());

        tags1.setOnDragListener(new MyInterestsFragment.MyDragListener());
        tags2.setOnDragListener(new MyInterestsFragment.MyDragListener());
        tags3.setOnDragListener(new MyInterestsFragment.MyDragListener());
        tags4.setOnDragListener(new MyInterestsFragment.MyDragListener());


        updateContainer(tags1);
        tags1.setBackgroundColor(Color.parseColor("#fbf2fc"));

        updateContainer(tags2);
        tags2.setBackgroundColor(Color.parseColor("#fffbef"));

        updateContainer(tags3);
        tags3.setBackgroundColor(Color.parseColor("#fff4f6"));

        updateContainer(tags4);
        tags4.setBackgroundColor(Color.parseColor("#f7fff7"));


        final ArrayList<String> allInterests = PeevesList.getNewInterestsTopics();

        ArrayList<String> interestsList1 = new ArrayList<String>();
        ArrayList<String> interestsList2 = new ArrayList<String>();
        ArrayList<String> interestsList3 = new ArrayList<String>();
        ArrayList<String> interestsList4 = new ArrayList<String>();

        final Set<String> interests1 = sp.getStringSet("interests1", new HashSet<String>());
        interestsList1.addAll(interests1);

        final Set<String> interests2 = sp.getStringSet("interests2", new HashSet<String>());
        interestsList2.addAll(interests2);

        final Set<String> interests3 = sp.getStringSet("interests3", new HashSet<String>());
        interestsList3.addAll(interests3);

        tags3.removeAllTags();
        tags3.setTags(interestsList1);

        tags2.removeAllTags();
        tags2.setTags(interestsList2);

        tags4.removeAllTags();
        tags4.setTags(interestsList3);

        interestsList4.clear();
        for(String s: allInterests){
            if(!interestsList1.contains(s) && !interestsList2.contains(s) && !interestsList3.contains(s)){
                interestsList4.add(s);
            }
        }
        tags1.setTags(interestsList4);




        setListenerToTags(tags1, 1);
        setListenerToTags(tags2, 2);
        setListenerToTags(tags3, 3);
        setListenerToTags(tags4, 4);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show();

                ArrayList<String> interests_list1 = new ArrayList<String>();
                ArrayList<String> interests_list2 = new ArrayList<String>();
                ArrayList<String> interests_list3 = new ArrayList<String>();

                interests_list1.clear();
                interests_list2.clear();
                interests_list3.clear();

                interests_list1 = (ArrayList<String>) tags3.getTags();
                interests_list2 = (ArrayList<String>) tags2.getTags();
                interests_list3 = (ArrayList<String>) tags4.getTags();

                Map<String,Object> interest1 = new HashMap<String,Object>();
                Map<String,Object> interest2 = new HashMap<String,Object>();
                Map<String,Object> interest3 = new HashMap<String,Object>();
                Firebase interestsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/interests/");

                interestsRef.child("interests1").setValue(null);
                interestsRef.child("interests2").setValue(null);
                interestsRef.child("interests3").setValue(null);

                for(String s1: interests_list1){
                    interest1.put(s1,"1");
                }
                for(String s2: interests_list2){
                    interest2.put(s2,"1");
                }
                for(String s3: interests_list3){
                    interest3.put(s3,"1");
                }

                interestsRef.child("interests1").updateChildren(interest1);
                interestsRef.child("interests2").updateChildren(interest2);
                interestsRef.child("interests3").updateChildren(interest3);

                sp.edit().putStringSet("interests1", new HashSet<String>(interests_list1))
                        .putStringSet("interests2", new HashSet<String>(interests_list2))
                        .putStringSet("interests3", new HashSet<String>(interests_list3))
                        .apply();


            }
        });
    }

    private void setListenerToTags(TagContainerLayout tags, int position ) {
        for (int i = 0; i<tags.getTags().size(); i++){
            TagView tag = tags.getTagView(i);
            tag.setBorderRadius(25.0f);
            tag.setOnTouchListener(new MyInterestsFragment.MyTouchListener());
            if(position == 1){
                /*tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.parseColor("#5C6BC0"));//Blue
                tag.setTagBorderColor(Color.parseColor("#5C6BC0"));*/
                tag.setTagBackgroundColor(Color.parseColor("#7986CB"));
                tag.setTagTextColor(Color.WHITE);//Blue
                tag.setTagBorderColor(Color.WHITE);
            }else if(position == 2){
                tag.setTagBackgroundColor(Color.parseColor("#FFD54F"));
                tag.setTagTextColor(Color.WHITE);//Blue
                tag.setTagBorderColor(Color.WHITE);
                /*tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.parseColor("#FFCA28"));//Yellow
                tag.setTagBorderColor(Color.parseColor("#FFCA28"));*/
            }else if(position == 3){
                /*tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.parseColor("#EF5350"));//Red
                tag.setTagBorderColor(Color.parseColor("#EF5350"));*/
                tag.setTagBackgroundColor(Color.parseColor("#E57373"));
                tag.setTagTextColor(Color.WHITE);//Blue
                tag.setTagBorderColor(Color.WHITE);
            }else if(position == 4){
                tag.setTagBackgroundColor(Color.parseColor("#81C784"));
                tag.setTagTextColor(Color.WHITE);//Blue
                tag.setTagBorderColor(Color.WHITE);
                /*tag.setTagBackgroundColor(Color.WHITE);
                tag.setTagTextColor(Color.parseColor("#66BB6A"));
                tag.setTagBorderColor(Color.parseColor("#66BB6A"));*/
            }
        }
    }

    private void updateContainer(TagContainerLayout mTagContainerLayoutAllTags) {
        mTagContainerLayoutAllTags.setRippleDuration(100);
        mTagContainerLayoutAllTags.setVerticalInterval(3.0f);
        mTagContainerLayoutAllTags.setHorizontalInterval(3.0f);
        mTagContainerLayoutAllTags.setBorderColor(Color.TRANSPARENT);
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                /*view.setVisibility(View.INVISIBLE);*/
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        /*Drawable enterShape = getResources().getDrawable(
                R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);*/

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    String s = "";
                    String s1 = s;
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

                        List<String> list = ((TagContainerLayout)owner).getTags();
                        for(int j = 0; j<list.size(); j++){
                            if(list.get(j).equalsIgnoreCase(((TagView)view).getText()) ){
                                ((TagContainerLayout)owner).removeTag(j);
                            }
                        }

                        TagContainerLayout container = (TagContainerLayout) v;
                        container.addTag(((TagView) view).getText(), 0);
                        /*view.setVisibility(View.VISIBLE);*/
                        TagView tag = container.getTagView(0);
                        if (container.getId() == R.id.tags1) {
                            setListenerToTags(container, 1);
                        }else if(container.getId() == R.id.tags2){
                            setListenerToTags(container, 2);
                        }else if(container.getId() == R.id.tags3){
                            setListenerToTags(container, 3);
                        }else if(container.getId() == R.id.tags4){
                            setListenerToTags(container, 4);
                        }


                        //updateContainer(container);
                    }else if(v instanceof ScrollView){
                        int childCount = ((ScrollView) v).getChildCount();
                        for(int i = 0 ; i<childCount; i++){
                            View v1 = ((ScrollView) v).getChildAt(i);
                            if(v1 instanceof TagContainerLayout){
                                View view = (View) event.getLocalState();
                                ViewGroup owner = (ViewGroup) view.getParent();
                                //owner.removeView(view);
                                List<String> list = ((TagContainerLayout)owner).getTags();
                                for(int j = 0; j<list.size(); j++){
                                    if(list.get(j).equalsIgnoreCase(((TagView)view).getText()) ){
                                        ((TagContainerLayout)owner).removeTag(j);
                                    }
                                }
                                TagContainerLayout container = (TagContainerLayout) v1;
                                container.addTag(((TagView) view).getText(), 0);
                                /*view.setVisibility(View.VISIBLE);*/
                                TagView tag = container.getTagView(0);
                                if (container.getId() == R.id.tags1) {
                                    setListenerToTags(container, 1);
                                }else if(container.getId() == R.id.tags2){
                                    setListenerToTags(container, 2);
                                }else if(container.getId() == R.id.tags3){
                                    setListenerToTags(container, 3);
                                }else if(container.getId() == R.id.tags4){
                                    setListenerToTags(container, 4);
                                }

                                //updateContainer(container);
                            }
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);
                default:
                    String s3 = "";
                    break;
            }
            return true;
        }
    }


}
