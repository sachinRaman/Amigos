package com.amigos.sachin.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.amigos.sachin.Activities.ChatListActivity;
import com.amigos.sachin.Activities.MainActivity;
import com.amigos.sachin.Activities.MainTabsActivity;
import com.amigos.sachin.Activities.PeopleWhoLikedMeActivity;
import com.amigos.sachin.Activities.SplashScreen2;
import com.amigos.sachin.Activities.UserProfileActivity;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.ChatsFragments.MyChatFragment;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.NotificationVO;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Sachin on 8/30/2017.
 */

public class ChatService extends Service {

    String myId;
    Context context;
    DataSnapshot prevDataSnapshot;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        context = getApplicationContext();

        Firebase.setAndroidContext(context);

        final Firebase myChatRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+myId+"/");

        /*myChatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int chatNumber = 0;
                int messageNumber = 0;
                ArrayList<NotificationVO> notificationVOArrayList = new ArrayList<NotificationVO>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    chatNumber++;
                    String id = child.getKey();
                    for(DataSnapshot snap : child.getChildren()){
                        String userName = snap.getKey();
                        String time = null;
                        String message = "";
                        for(DataSnapshot data : snap.getChildren()){
                            time = data.getKey();
                            message +="\t"+ data.getValue().toString() + "\n";
                            messageNumber++;
                            ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
                            chatUsersDAO.addToChatList(id,myId,data.getValue().toString(),1);
                        }
                        NotificationVO notificationVO = new NotificationVO();
                        notificationVO.id = id;
                        notificationVO.name = userName;
                        notificationVO.message = message;
                        notificationVO.time = time;
                        notificationVOArrayList.add(notificationVO);
                    }
                }
                *//*Collections.sort(notificationVOArrayList, new Comparator<NotificationVO>() {
                    @Override
                    public int compare(NotificationVO lhs, NotificationVO rhs) {
                        if ( lhs.time.compareTo(rhs.time) > 0 )
                            return -1;
                        return 1;
                    }
                });*//*
                if (chatNumber > 0) {
                    sendNotification(chatNumber, messageNumber, notificationVOArrayList);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int i = 0;
                i = 5*2;
                int j = i;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/

        myChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (prevDataSnapshot == null){
                    prevDataSnapshot = dataSnapshot;
                }
                if(dataSnapshot!= null){
                    if(dataSnapshot.toString().equalsIgnoreCase(prevDataSnapshot.toString())){
                        return;
                    }else{
                        prevDataSnapshot = dataSnapshot;
                    }
                }
                int chatNumber = 0;
                int messageNumber = 0;
                ArrayList<NotificationVO> notificationVOArrayList = new ArrayList<NotificationVO>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    chatNumber++;
                    String id = child.getKey();
                    for(DataSnapshot snap : child.getChildren()){
                        String userName = snap.getKey();
                        String time = null;
                        String message = "";
                        for(DataSnapshot data : snap.getChildren()){
                            time = data.getKey();
                            message +="\t"+ data.getValue().toString() + "\n";
                            messageNumber++;
                            ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
                            chatUsersDAO.addToChatList(id,myId,data.getValue().toString(),1);
                        }
                        NotificationVO notificationVO = new NotificationVO();
                        notificationVO.id = id;
                        notificationVO.name = userName;
                        notificationVO.message = message;
                        notificationVO.time = time;
                        notificationVOArrayList.add(notificationVO);
                    }
                }
                Collections.sort(notificationVOArrayList, new Comparator<NotificationVO>() {
                    @Override
                    public int compare(NotificationVO lhs, NotificationVO rhs) {
                        if ( lhs.time.compareTo(rhs.time) > 0 )
                            return -1;
                        return 1;
                    }
                });
                if (chatNumber > 0) {
                    sendNotification(chatNumber, messageNumber, notificationVOArrayList);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final Firebase myLikedRef = new Firebase("https://new-amigos.firebaseio.com/liked_notifications/"+myId+"/");
        myLikedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String userId = data.getKey();
                    for (DataSnapshot snap : data.getChildren()){
                        String userName = snap.getKey();
                        String time = snap.getValue().toString();
                        sendLikedNotification(userId,userName, time);
                    }
                }
                myLikedRef.setValue(null);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.i("ChatService","onTaskRemoved of chatservice");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent =  PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    public void sendNotification(int chatNumber, int messageNumber, ArrayList<NotificationVO> notificationVOArrayList){

        Intent notificationIntent = new Intent(getApplicationContext(), ChatListActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        /*notificationIntent.putExtra("tab",2);
        notificationIntent.putExtra("bottomTab",0);*/
        //int id = (int) System.currentTimeMillis();
        int id = 12345;
        String message = "";
        for(NotificationVO notificationVO: notificationVOArrayList){
            message += notificationVO.name +" :\n";
            message += notificationVO.message;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            String title = null;
            if(chatNumber == 1){
                if (messageNumber == 1){
                    title = "You have "+messageNumber + " message from "+chatNumber+" chat";
                }else{
                    title = "You have "+messageNumber + " messages from "+chatNumber+" chat";
                }
            }else{
                if (messageNumber == 1){
                    title = "You have "+messageNumber + " message from "+chatNumber+" chats";
                }else{
                    title = "You have "+messageNumber + " messages from "+chatNumber+" chats";
                }
            }

            NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setTicker("Amigos").setWhen(0)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            //alarmNotificationBuilder.setContentText("Message :" + message).setNumber(++numMessages);

            alarmNotificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            Notification newMessageNotification=alarmNotificationBuilder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            newMessageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(id, newMessageNotification);

        }
    }

    public void sendLikedNotification(String userId, final String name, String time){
        Intent notificationIntent = new Intent(getApplicationContext(), PeopleWhoLikedMeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        //int id = (int) System.currentTimeMillis();

        ApplicationCache.loadMyUserVO();

        ApplicationCache.loadThisUserVO(userId);

        int id = 0;

        time = time.replace("_","");
        if(time.length() >= 8) {
            time = time.substring(time.length() - 8);
            id = Integer.parseInt(time);
        }

        final Intent intent  = new Intent(this, UserProfileActivity.class);
        intent.putExtra("userId",userId);
        /*intent.putExtra("tab",2);
        intent.putExtra("bottomTab",2);*/

        Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/");
        final int finalId = id;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userData : dataSnapshot.getChildren()){
                    if("interests_list".equalsIgnoreCase(userData.getKey())){
                        ArrayList<String> interests = new ArrayList<String>();
                        interests.clear();
                        for (DataSnapshot interest_list : userData.getChildren()){
                            String key = interest_list.getKey();
                            Map<String, String> topicInterests = interest_list.getValue(Map.class);
                            for (String s: topicInterests.keySet()){
                                if("1".equalsIgnoreCase(topicInterests.get(s))){
                                    interests.add(s);
                                }
                            }
                        }
                        ApplicationCache.myUserVO.setInterests(interests);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), finalId, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                            String title = null;
                            String message = name + " admired your profile.";

                            NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.logo)
                                    .setTicker("Amigos").setWhen(0)
                                    .setContentTitle("Amigos")
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                    .setContentText(message)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            //alarmNotificationBuilder.setContentText("Message :" + message).setNumber(++numMessages);

                            alarmNotificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
                            Notification newMessageNotification=alarmNotificationBuilder.build();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            newMessageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                            notificationManager.notify(finalId, newMessageNotification);

                            //MyChatFragment.reloadChatList();
                            //ChatListActivity.reloadChatList();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
