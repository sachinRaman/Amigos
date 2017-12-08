package com.amigos.sachin.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.amigos.sachin.Activities.ChatListActivity;
import com.amigos.sachin.ChatsFragments.MyChatFragment;
import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.NotificationVO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Sachin on 8/30/2017.
 */

public class ChatService extends Service {

    String myId;
    Context context;
    DataSnapshot prevDataSnapshot;
    public static String thisUserId = null;
    String TAG = "ChatService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        String s = "";
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        context = getApplicationContext();

        Firebase.setAndroidContext(context);
        final Firebase myChatRef = new Firebase("https://new-amigos.firebaseio.com/message_notification/"+myId+"/");

        /*myChatRef.addValueEventListener(new ValueEventListener() {
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
                        MyChatFragment.reloadChat();
                        NotificationVO notificationVO = new NotificationVO();
                        notificationVO.id = id;
                        notificationVO.name = userName;
                        notificationVO.message = message;
                        notificationVO.time = time;
                        if(thisUserId != null){
                            if(!thisUserId.equalsIgnoreCase(id)){
                                notificationVOArrayList.add(notificationVO);
                            }
                        }else{
                            notificationVOArrayList.add(notificationVO);
                        }
                    }
                }
                Collections.sort(notificationVOArrayList, new Comparator<NotificationVO>() {
                    @Override
                    public int compare(NotificationVO lhs, NotificationVO rhs) {
                        if ( lhs.time.compareTo(rhs.time) > 0 ) {
                            return -1;
                        }else if(lhs.time.compareTo(rhs.time) < 0){
                            return 1;
                        }
                        return 0;
                    }
                });
                if (chatNumber > 0 ) {
                    sendNotification(chatNumber, messageNumber, notificationVOArrayList);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent startChatService = new Intent(this, EnablingService.class);
        startService(startChatService);
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onDestroy() {
        Intent startChatService = new Intent(this, ChatService.class);
        startService(startChatService);
    }

    public void sendNotification(int chatNumber, int messageNumber, ArrayList<NotificationVO> notificationVOArrayList){

        Intent notificationIntent = new Intent(getApplicationContext(), ChatListActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        int id = 12345;
        String message = "";

        for(NotificationVO notificationVO: notificationVOArrayList){
            message += notificationVO.name +" :\n";
            message += notificationVO.message;
            final String userId = notificationVO.id;
            String key = notificationVO.time;
            Firebase reachedMessageIdRef = new Firebase("https://new-amigos.firebaseio.com/users_chats/"+userId+"/"+myId+ "/chat_status/reached_message_id/");
            reachedMessageIdRef.setValue(key);
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

            alarmNotificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            Notification newMessageNotification=alarmNotificationBuilder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            newMessageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(id, newMessageNotification);
        }
    }
}
