package com.amigos.sachin.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.MyProfileFragments.MyMoods;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropSquareTransformation;


/**
 * Created by Sachin on 9/2/2017.
 */

public class BlockListArrayAdapter extends ArrayAdapter<LikedUserVO> implements View.OnClickListener {

    ArrayList<LikedUserVO> blockedUsersList;
    Context context;
    ListView blockedListView;
    private static LayoutInflater inflater=null;
    String myId;

    public BlockListArrayAdapter(Context ctx, ArrayList<LikedUserVO> blockedList, ListView blockedListView) {
        super(ctx, R.layout.item_chat_list, blockedList);
        this.blockedUsersList =blockedList;
        this.blockedListView = blockedListView;
        context=ctx;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BlockedListViewHolder holder;

        if (convertView == null){
            holder = new BlockedListViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_chat_list, parent, false);
            SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
            myId = sp.getString("myId","");

            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvMatch = (TextView) convertView.findViewById(R.id.tv_match_info);
            holder.profilePicImageView = (ImageView) convertView.findViewById(R.id.iv_profile);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time_last_message);
            holder.view = (LinearLayout)convertView.findViewById(R.id.holder_LinearLayout);
            convertView.setTag(holder);
        }else{
            holder = (BlockedListViewHolder) convertView.getTag();
        }

        holder.tvName.setText("");
        holder.tvTime.setText("");
        holder.tvMatch.setText("");
        holder.tvStatus.setText("");

        final String userId  = blockedUsersList.get(position).getId();

        Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if("name".equalsIgnoreCase(child.getKey())){
                        if(child.getValue().toString() != null && !child.getValue().toString().isEmpty()) {
                            holder.tvName.setText(child.getValue().toString());
                        }else{
                            holder.tvName.setText("User");
                        }
                    }
                    if("status".equalsIgnoreCase(child.getKey())){
                        holder.tvStatus.setText(child.getValue().toString());
                    }
                    if("imageUrl".equalsIgnoreCase(child.getKey())){
                        for(DataSnapshot children : child.getChildren()){
                            if(userId.equalsIgnoreCase(children.getKey())){
                                String imageUrl = children.getValue().toString();
                                Glide.with(context).load(imageUrl)
                                        .bitmapTransform(new CropSquareTransformation(context)).thumbnail(0.5f).crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profilePicImageView);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

        userRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if("name".equalsIgnoreCase(child.getKey())){
                        if(child.getValue().toString() != null && !child.getValue().toString().isEmpty()) {
                            holder.tvName.setText(child.getValue().toString());
                        }else{
                            holder.tvName.setText("User");
                        }
                    }
                    if("status".equalsIgnoreCase(child.getKey())){
                        holder.tvStatus.setText(child.getValue().toString());
                    }
                    if("imageUrl".equalsIgnoreCase(child.getKey())){
                        for(DataSnapshot children : child.getChildren()){
                            if(userId.equalsIgnoreCase(children.getKey())){
                                String imageUrl = children.getValue().toString();
                                Glide.with(context).load(imageUrl)
                                        .bitmapTransform(new CropSquareTransformation(context)).thumbnail(0.5f).crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profilePicImageView);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context,holder.view);
                popup.getMenuInflater().inflate(R.menu.unblock_popup_menu, popup.getMenu());
                //Toast.makeText(ChatActivity.this,"You Clicked : ",Toast.LENGTH_SHORT).show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.unblock:
                                Toast.makeText(context,"You have successfully unblocked "+ holder.tvName.getText().toString(),1000).show();
                                Firebase myBlockListRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/block_list"
                                        +"/people_i_blocked/");
                                Firebase userBlockListRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/block_list"
                                        +"/people_who_blocked_me/");
                                myBlockListRef.child(userId).setValue(null);
                                userBlockListRef.child(myId).setValue(null);
                                for(int i = ApplicationCache.peopleIBlockedList.size()-1; i>=0; i--){
                                    String thisUserId = ApplicationCache.peopleIBlockedList.get(i);
                                    if(userId.equalsIgnoreCase(thisUserId)){
                                        ApplicationCache.peopleIBlockedList.remove(i);

                                    }
                                }
                                MyMoods.reloadData();
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        return convertView;

    }

    @Override
    public void onClick(View v) {

    }

    public static class BlockedListViewHolder {
        TextView tvName;
        TextView tvMatch;
        TextView tvStatus;
        TextView tvTime;
        ImageView profilePicImageView;
        View view;

        String userId;

        public String getUserId(){
            return userId;
        }

        private void setUserId(String userId) {
            this.userId = userId;
        }

    }
}
