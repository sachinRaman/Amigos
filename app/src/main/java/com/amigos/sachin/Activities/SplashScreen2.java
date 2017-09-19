package com.amigos.sachin.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.R;
import com.amigos.sachin.Utils.ToastHandler;

public class SplashScreen2 extends AppCompatActivity {

    String myId;
    SharedPreferences sp;
    int tab = 1;
    int bottomTab = 0;
    Context context;
    ProgressDialog progress;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);
        context = getApplicationContext();
        getSupportActionBar().hide();

        /*progress = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        progress.setTitle("Loading");
        progress.setMessage("Data is loading please wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();*/

        progressBar = (ProgressBar) findViewById(R.id.progressBarSplashScreen2);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        tab = intent.getIntExtra("tab",1);
        bottomTab = intent.getIntExtra("bottomTab",0);
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

            ApplicationCache.setMyId(myId);
            ApplicationCache applicationCache = new ApplicationCache();


            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            while(ApplicationCache.userVOArrayList.isEmpty()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"You have poor internet connection.\nLoading the data might take some time.",1000).show();
                    }
                });
                /*ToastHandler mToastHandler = new ToastHandler(context);
                mToastHandler.showToast("Internet connection is poor",1000);*/
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }

            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            /*progress.dismiss();*/
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(SplashScreen2.this, MainTabsActivity.class);
            intent.putExtra("data", result);
            intent.putExtra("tab",tab);
            intent.putExtra("bottomTab",bottomTab);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
