package com.amigos.sachin.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amigos.sachin.R;
import com.amigos.sachin.VO.NotificationVO;

import java.util.ArrayList;

/**
 * Created by Sachin on 10/4/2017.
 */

public class NotificationsAdapter extends ArrayAdapter<NotificationVO> implements View.OnClickListener {

    ArrayList<NotificationVO> notificationVOArrayList;
    Context context;
    ListView notificationListView;
    private static LayoutInflater inflater=null;


    public NotificationsAdapter(Context ctx, ArrayList<NotificationVO> notificationVOArrayList, ListView notificationListView) {
        super(ctx, R.layout.item_message,notificationVOArrayList);
        this.notificationVOArrayList =notificationVOArrayList;
        this.notificationListView = notificationListView;
        context=ctx;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NotificationsAdapter.NotificationMessageHolder holder;

        if (convertView == null){
            holder = new NotificationsAdapter.NotificationMessageHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_chat_list, parent, false);

            holder.tvName = (TextView) convertView.findViewById(R.id.notification_name);
            holder.tvMessage = (TextView) convertView.findViewById(R.id.notification_message);
            /*holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time_last_message);*/
            holder.view = (LinearLayout)convertView.findViewById(R.id.notification_holder);
            convertView.setTag(holder);
        }else{
            holder = (NotificationsAdapter.NotificationMessageHolder) convertView.getTag();
        }

        NotificationVO notificationVO = notificationVOArrayList.get(position);

        holder.tvName.setText(notificationVO.name);
        holder.tvMessage.setText(notificationVO.message);
        holder.userId = notificationVO.id;

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    public static class NotificationMessageHolder {
        TextView tvName;
        TextView tvMessage;
        TextView tvTime;
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
