package com.amigos.sachin.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.amigos.sachin.Activities.ChatActivity;
import com.amigos.sachin.Activities.ChatListActivity;
import com.amigos.sachin.Activities.UserProfileActivity;
import com.amigos.sachin.DAO.ChatNotificationsDAO;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.NotificationVO;
import com.firebase.client.Firebase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Sachin on 10/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Context context;
    String myId;
    String TAG = "FirebaseMsgService";
    String thisUserId = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        context = getApplicationContext();
        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        Firebase.setAndroidContext(context);
        Firebase myAdmiredNotificationRef = new Firebase("https://new-amigos.firebaseio.com/admired_notifications/"+myId+"/");

        String userId = "", token = null, userName = null, timeStamp = null, message = "", type="", title="Amigos";
        String key ="", prevKey = "";
        if(remoteMessage.getData().size()>0){
            Map<String , String> payload = remoteMessage.getData();
            type = payload.get("type");
            message = payload.get("message"); // sender's name will be sent in case of push message notification
            userId = payload.get("userId");
            token = payload.get("token");
            timeStamp = payload.get("time");
            title = payload.get("title");
            key = payload.get("key");
            prevKey = payload.get("prevKey");
            userName = payload.get("userName");
        }
        if("admired_notification".equalsIgnoreCase(type)) {
            sendAdmiredNotification(message, title, userId);
            myAdmiredNotificationRef.setValue(null);
        }
        if("push_message_notification".equalsIgnoreCase(type)){
            ChatNotificationsDAO chatNotificationsDAO = new ChatNotificationsDAO(context);
            chatNotificationsDAO.addToNotificationList(userId, userName, message, timeStamp);
            chatNotificationsDAO.removeDuplicateEntries();
            ArrayList<NotificationVO> notificationVOArrayList = chatNotificationsDAO.getAllNotificationsList();
            sendMessageNotification(notificationVOArrayList);
            Firebase messagePushNotificationRef = new Firebase("https://new-amigos.firebaseio.com/push_message_notification/"+myId+"/");
            messagePushNotificationRef.setValue(null);
        }
    }

    public void sendMessageNotification(ArrayList<NotificationVO> notificationVOArrayList){
        int messageCount = 0;
        int chatCount = 0;
        String title = "";
        String finalMessage = "";

        for(NotificationVO notificationVO: notificationVOArrayList){
            String timeStamp = null;
            try {
                thisUserId = ChatActivity.currentUserId;
            }catch (Exception e){

            }
            if(notificationVO.getId().equalsIgnoreCase(thisUserId)){
               continue;
            }
            chatCount++;
            finalMessage += notificationVO.getName() + "\n";
            timeStamp = notificationVO.getTime();
            for(String s: notificationVO.getMessageArrayList()){
                messageCount++;
                finalMessage += "\t" + s + "\n";
                ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
                chatUsersDAO.addToChatList(notificationVO.getId(),myId,s,1);
            }
            Firebase reachedMessageIdRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+notificationVO.getId()+"/"+myId+ "/chat_status/reached_message_id/");
            reachedMessageIdRef.setValue(timeStamp);
        }
        //MyChatFragment.reloadChat();


        if(chatCount <= 1){
            if(messageCount <= 1){
                title = "You have "+messageCount+ " message from " +chatCount+ " chat";
            }else{
                title = "You have "+messageCount+ " messages from " +chatCount+ " chat";
            }
        }else{
            title = "You have "+messageCount+ " messages from " +chatCount+ " chats";
        }

        if(chatCount > 0) {

            Intent notificationIntent = new Intent(context, ChatListActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            int id = 12345;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(finalMessage))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notificationBuilder.build());
        }

        Intent intent = new Intent("reloadChatList");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void sendAdmiredNotification(String message, String title, String userId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("userId",userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        Firebase admiredNotificationRef = new Firebase("https://new-amigos.firebaseio.com/admired_notifications/"
                + myId + "/");
        admiredNotificationRef.setValue(null);
    }
}
