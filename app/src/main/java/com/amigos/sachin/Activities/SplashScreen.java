package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amigos.sachin.DAO.InterestsDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.Values.PeevesList;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    String TAG = "SplashScreen";
    String myId = "";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        startHeavyProcessing();
    }

    private void startHeavyProcessing(){
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String

            sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
            myId = sp.getString("myId", "");

            ArrayList<String> lifestyle = PeevesList.getAllLifestyleInterests();
            ArrayList<String> arts = PeevesList.getAllArtsInterests();
            ArrayList<String> entertainment = PeevesList.getAllEntertainmentInterests();
            ArrayList<String> business = PeevesList.getAllBusinessInterests();
            ArrayList<String> sports = PeevesList.getAllSportsInterests();
            ArrayList<String> music = PeevesList.getAllMusicInterests();
            ArrayList<String> technology = PeevesList.getAllTechnologyInterests();

            addInterestsToCloud(myId,"lifestyle",lifestyle);
            addInterestsToCloud(myId,"arts",arts);
            addInterestsToCloud(myId,"entertainment",entertainment);
            addInterestsToCloud(myId,"business",business);
            addInterestsToCloud(myId,"sports",sports);
            addInterestsToCloud(myId,"music",music);
            addInterestsToCloud(myId,"technology",technology);

            addInterestsToDB("lifestyle",lifestyle);
            addInterestsToDB("arts",arts);
            addInterestsToDB("entertainment",entertainment);
            addInterestsToDB("business",business);
            addInterestsToDB("sports",sports);
            addInterestsToDB("music",music);
            addInterestsToDB("technology",technology);
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(SplashScreen.this, MainTabsActivity.class);
            intent.putExtra("data", result);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void addInterestsToCloud(String myId, String topic, ArrayList<String> list){
        Firebase interestsRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/interests_list/" + topic + "/");
        for (String s : list){
            interestsRef.child(s).setValue("0");
        }

        //For fake profiles
        /*for(int i = 0;i<25; i++) {
            Firebase ref = new Firebase("https://new-amigos.firebaseio.com/users/test" + i + "/interests_list/" + topic + "/");
            for (String s : list){
                int n = 0 + (int)(Math.random() * ((1 - 0) + 1));
                ref.child(s).setValue(Integer.toString(n));
            }
        }*/
    }

    public void addInterestsToDB(String tag,ArrayList<String> list){
        InterestsDAO interestsDAO = new InterestsDAO(getApplicationContext());
        //interestsDAO.clearTableData();
        for (String s : list){
            interestsDAO.addInterestsToDB(tag,s,0);
        }
    }
}
