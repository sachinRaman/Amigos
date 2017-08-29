package com.amigos.sachin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.R;
import com.amigos.sachin.VO.LikedUserVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropSquareTransformation;

/**
 * Created by Sachin on 8/30/2017.
 */

public class LikedUsersLVAdapter extends ArrayAdapter<LikedUserVO> implements View.OnClickListener {

    ArrayList<LikedUserVO> likedUsersVOList;
    Context context;
    ListView likedListView;
    private static LayoutInflater inflater=null;

    public LikedUsersLVAdapter(Context ctx, ArrayList<LikedUserVO> chatVoList, ListView chatListView) {
        super(ctx, R.layout.item_chat_list,chatVoList);
        this.likedUsersVOList =chatVoList;
        this.likedListView = chatListView;
        context=ctx;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LikedUsersLVAdapter.LikedListViewHolder holder;

        if (convertView == null){
            holder = new LikedUsersLVAdapter.LikedListViewHolder();

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
            holder = (LikedUsersLVAdapter.LikedListViewHolder) convertView.getTag();
        }

        holder.tvName.setText("");
        final String[] userName = {""};
        LikedUserVO likedUsersVO = likedUsersVOList.get(position);
        holder.tvTime.setText("");
        //holder.tvStatus.setText(likedUsersVO.getStatus());
        holder.tvMatch.setText("");
        holder.tvStatus.setText("");

        final String userId = likedUsersVO.getId();
        final String[] imageUrl = {""};

        Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+userId+"/");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if("name".equalsIgnoreCase(child.getKey())){
                        if(child.getValue().toString() != null && !child.getValue().toString().isEmpty()) {
                            holder.tvName.setText(child.getValue().toString());
                            userName[0] = child.getValue().toString();
                        }else{
                            holder.tvName.setText("User");
                            userName[0] = "User";
                        }
                    }
                    if("status".equalsIgnoreCase(child.getKey())){
                        holder.tvStatus.setText(child.getValue().toString());
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        /*holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("userName",userName[0]);
                intent.putExtra("imageUrl",imageUrl[0]);
                context.startActivity(intent);
            }
        });*/

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    public static class LikedListViewHolder {
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
