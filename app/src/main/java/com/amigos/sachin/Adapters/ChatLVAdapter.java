package com.amigos.sachin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.Activities.ChatActivity;
import com.amigos.sachin.Activities.UserProfileActivity;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatUsersVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.firebase.client.DataSnapshot;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/
//import com.firebase.client.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropSquareTransformation;

/**
 * Created by Sachin on 8/28/2017.
 */

public class ChatLVAdapter extends ArrayAdapter<ChatUsersVO> implements View.OnClickListener {

    ArrayList<ChatUsersVO> chatUsersVoList;
    Context context;
    ListView chatListView;
    private static LayoutInflater inflater=null;

    public ChatLVAdapter(Context ctx, ArrayList<ChatUsersVO> chatVoList, ListView chatListView) {
        super(ctx, R.layout.item_chat_list,chatVoList);
        this.chatUsersVoList=chatVoList;
        this.chatListView = chatListView;
        context=ctx;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatListViewHolder holder;

        if (convertView == null){
            holder = new ChatListViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_chat_list, parent, false);

            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvMatch = (TextView) convertView.findViewById(R.id.tv_match_info);
            holder.profilePicImageView = (ImageView) convertView.findViewById(R.id.iv_profile);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time_last_message);
            holder.view = (LinearLayout)convertView.findViewById(R.id.holder_LinearLayout);
            convertView.setTag(holder);
        }else{
            holder = (ChatListViewHolder) convertView.getTag();
        }

        //holder.tvName.setText("");
        final String[] userName = {""};
        ChatUsersVO chatUsersVO = chatUsersVoList.get(position);

        String newDateString = chatUsersVO.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date d = null;
        try {
            d = sdf.parse(chatUsersVO.getTime());
            sdf.applyPattern("dd MMM");
            newDateString = sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Typeface typeFaceCalibri = Typeface.createFromAsset(context.getAssets(),"fonts/Calibri/Calibri.ttf");
        holder.tvTime.setTypeface(typeFaceCalibri);
        holder.tvName.setTypeface(typeFaceCalibri);
        holder.tvStatus.setTypeface(typeFaceCalibri);
        holder.tvMatch.setTypeface(typeFaceCalibri);

        holder.tvTime.setText(newDateString);
        holder.tvStatus.setText(chatUsersVO.getLastMessage());
        holder.tvMatch.setText("");
        if(chatUsersVO.getSeen() == 1){
            holder.tvMatch.setText("New Message");
            holder.tvMatch.setTextColor(Color.parseColor("#40a8e0"));
        }
        final String userId = chatUsersVO.getUserId();
        final String[] imageUrl = {""};

        Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if("name".equalsIgnoreCase(child.getKey())){
                        if(child.getValue().toString() != null && !child.getValue().toString().isEmpty()) {
                            holder.tvName.setText(child.getValue().toString());
                            //holder.tvName.setTextColor(Color.parseColor("#757575"));
                            userName[0] = child.getValue().toString();
                        }else{
                            holder.tvName.setText("User");
                            userName[0] = "User";
                        }
                    }
                    if("imageUrl".equalsIgnoreCase(child.getKey())){
                        for(DataSnapshot children : child.getChildren()){
                            if(userId.equalsIgnoreCase(children.getKey())){
                                imageUrl[0] = children.getValue().toString();
                                Glide.with(context).load(imageUrl[0])
                                        .bitmapTransform(new CropSquareTransformation(context)).thumbnail(0.5f).crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profilePicImageView);
                            }
                        }
                    }
                }
                if(imageUrl[0].isEmpty()){
                    holder.profilePicImageView.setImageBitmap(null);
                    int imageResource1 = context.getResources().getIdentifier("@drawable/ic_user", null, context.getPackageName());
                    Drawable res1 = context.getResources().getDrawable(imageResource1);
                    holder.profilePicImageView.setImageDrawable(res1);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        /*DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

        firebaseRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if("name".equalsIgnoreCase(child.getKey())){
                        if(child.getValue().toString() != null && !child.getValue().toString().isEmpty()) {
                            holder.tvName.setText(child.getValue().toString());
                            //holder.tvName.setTextColor(Color.parseColor("#757575"));
                            userName[0] = child.getValue().toString();
                        }else{
                            holder.tvName.setText("User");
                            userName[0] = "User";
                        }
                    }
                    if("imageUrl".equalsIgnoreCase(child.getKey())){
                        for(DataSnapshot children : child.getChildren()){
                            if(userId.equalsIgnoreCase(children.getKey())){
                                imageUrl[0] = children.getValue().toString();
                                Glide.with(context).load(imageUrl[0])
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

        holder.profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserProfileActivity.class);
                intent.putExtra("userId",userId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("userName",userName[0]);
                intent.putExtra("imageUrl",imageUrl[0]);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    public static class ChatListViewHolder {
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
