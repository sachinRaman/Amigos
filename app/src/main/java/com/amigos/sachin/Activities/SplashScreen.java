package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.DAO.ChatsDAO;
import com.amigos.sachin.DAO.InterestsDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.Services.ChatService;
//import com.amigos.sachin.Services.EnablingService;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    String TAG = "SplashScreen";
    String myId = "";
    SharedPreferences sp;
    ProgressBar progressBar;
    Context context;
    Firebase myChatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = getApplicationContext();

        Firebase.setAndroidContext(context);

        Intent startChatService = new Intent(this, ChatService.class);
        startService(startChatService);
        getSupportActionBar().hide();

        sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId", "");

        myChatsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/chats/");

        progressBar = (ProgressBar) findViewById(R.id.progressBarSplashScreen);
        progressBar.setVisibility(View.VISIBLE);

        startHeavyProcessing();
    }

    private void startHeavyProcessing(){
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String

            /*myChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String userId = snapshot.getKey();

                        ChatsDAO chatsDAO = new ChatsDAO(context);
                        chatsDAO.deleteUserChat(userId);

                        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);

                        String messageId = "";
                        String message = "";
                        String time = "";
                        int seen = 1;
                        int userSide = 0;

                        for(DataSnapshot userData: snapshot.getChildren()) {
                            messageId = userData.getKey();
                            if(!messageId.equalsIgnoreCase(myId) && !messageId.equalsIgnoreCase(userId)){
                                for(DataSnapshot data: userData.getChildren()){
                                    if("-seen".equalsIgnoreCase(data.getKey())){
                                        seen = Integer.valueOf(data.getValue().toString());
                                    }
                                    if("-time".equalsIgnoreCase(data.getKey())){
                                        time = data.getValue().toString();
                                    }
                                    if(myId.equalsIgnoreCase(data.getKey())){
                                        message = data.getValue().toString();
                                        userSide = 0;
                                    }else if(userId.equalsIgnoreCase(data.getKey())){
                                        message = data.getValue().toString();
                                        userSide = 1;
                                    }
                                }
                            }
                            chatsDAO.addToChatList(userId,messageId,message,time,seen,userSide);
                        }
                        //chatUsersDAO.removeFromChatUsersDAO(userId);
                        chatUsersDAO.addToChatList(userId,myId,message,0);

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/



            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(SplashScreen.this, MyProfileActivity.class);
            intent.putExtra("data", result);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
