package com.amigos.sachin.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.amigos.sachin.R;
import com.amigos.sachin.Services.ChatService;
//import com.amigos.sachin.Services.EnablingService;
import com.amigos.sachin.Services.MyFirebaseInstanceIDService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    private String facebook_id,f_name, m_name, l_name, gender, profile_image, full_name, email_id;
    long fb_id;
    String profile_name;
    String birthday;
    String TAG = "Main Activity";
    int tab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Firebase.setAndroidContext(this);

        loginButton = (LoginButton)findViewById(R.id.button_fb_login);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        loginButton.setVisibility(View.VISIBLE);

        if (isLoggedIn()){
            if(isMyServiceRunning(ChatService.class) == false ) {
                Intent startChatService = new Intent(this, ChatService.class);
                startService(startChatService);
            }


            //startService(new Intent(ChatService.class.getName()));
            Intent intent = new Intent(MainActivity.this,SplashScreen2.class);
            intent.putExtra("tab",tab);
            startActivity(intent);
        }



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker profileTracker;
            @Override
            public void onSuccess(LoginResult loginResult) {
//                textView.setText("Login Success \n" + loginResult.getAccessToken().getUserId() + "\n" +
//                loginResult.getAccessToken().getToken());
                loginButton.setVisibility(View.GONE);


                Profile profile = Profile.getCurrentProfile();

                if (profile == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            if (currentProfile != null) {
                                facebook_id = currentProfile.getId();
                                f_name = currentProfile.getFirstName();
                                m_name = currentProfile.getMiddleName();
                                l_name = currentProfile.getLastName();
                                full_name = currentProfile.getName();
                                profile_image = currentProfile.getProfilePictureUri(400, 400).toString();
                                profileTracker.stopTracking();
                            }
                        }
                    };
                    profileTracker.startTracking();
                }else{
                    facebook_id = profile.getId();
                    f_name = profile.getFirstName();
                    m_name = profile.getMiddleName();
                    l_name = profile.getLastName();
                    full_name = profile.getName();
                    profile_image = profile.getProfilePictureUri(400, 400).toString();
                }
                //profileTracker.startTracking();
                /*Bundle params = new Bundle();
                new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                if (response != null) {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {
                                            String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                            //Bitmap profilePic = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                            // set profilePic bitmap to imageview
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();*/
                GraphRequestBatch batch = new GraphRequestBatch(


                /*GraphRequest request = */GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                Log.i("JSON", object.toString());



                                // Application code





                                try {
                                    if (object.has("email")){
                                        email_id=object.getString("email");
                                    }
                                    if (object.has("gender")){
                                        gender=object.getString("gender");
                                    }
                                    if (object.has("name")){
                                        profile_name=object.getString("name");
                                    }
                                    if (object.has("birthday")){
                                        birthday = object.getString("birthday");
                                    }
                                    //email_id=object.getString("email");
                                    //gender=object.getString("gender");
                                    //profile_name=object.getString("name");
                                    fb_id=object.getLong("id");
                                    //birthday = object.getString("birthday"); // 01/31/1980 format

                                    String myId = fb_id +"-"+profile_name;
                                    Firebase userRef = new Firebase("https://new-amigos.firebaseio.com/users/"+ myId+ "/");
                                    if(profile_name != null) {
                                        userRef.child("name").setValue(profile_name);
                                    }
                                    if (gender != null){
                                        userRef.child("sex").setValue(gender);
                                    }
                                    if (email_id != null){
                                        userRef.child("emailId").setValue(email_id);
                                    }
                                    if(profile_image != null){
                                        /*URL imageURL = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                        userRef.child("imageUrl").child(myId).setValue(imageURL.toString());*/
                                        userRef.child("imageUrl").child(myId).setValue(profile_image);
                                    }else {
                                        URL imageUrl = new URL("https://graph.facebook.com/"+fb_id+"/picture?height=400&width=400");
                                        userRef.child("imageUrl").child(myId).setValue(imageUrl.toString());
                                    }
                                    if(birthday != null){
                                        userRef.child("birthday").setValue(birthday);
                                        try {
                                            String[] ages = birthday.trim().split("/");
                                            String myAge = getAge(Integer.parseInt(ages[2]), Integer.parseInt(ages[0]), Integer.parseInt(ages[1]));
                                            userRef.child("age").setValue(myAge);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                    //Creating fake profiles
                                    /*if("1436936606392043-Sachin Raman".equalsIgnoreCase(myId)) {
                                        for (int i = 0; i < 25; i++) {

                                            *//*put condition for your id so changes can only be made
                                            from your phone*//*
                                            Firebase ref = new Firebase("https://new-amigos.firebaseio.com/users/test" + i + "/");
                                        *//*ref.setValue("0");*//*
                                            ref.setValue(null);
                                        *//*if(profile_name != null) {
                                            ref.child("name").setValue(profile_name);
                                        }
                                        if (gender != null){
                                            ref.child("sex").setValue(gender);
                                        }
                                        if (email_id != null){
                                            ref.child("emailId").setValue(email_id);
                                        }
                                        if(profile_image != null){
                                            ref.child("imageUrl").child(myId).setValue(profile_image);
                                        }
                                        if(birthday != null){
                                            ref.child("birthday").setValue(birthday);
                                        }*//*
                                        }
                                    }*/
                                    SharedPreferences sp=getApplicationContext().getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
                                    sp.edit().putString("myId",""+ myId)
                                            .putString("interestsUsageDirections","0").apply();
                                    startService(new Intent(ChatService.class.getName()));



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                /*Intent intent = new Intent(MainActivity.this,SplashScreen.class);
                                startActivity(intent);*/

                            }
                        }),

                GraphRequest.newMyFriendsRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {

                                String d = "123";
                                // Application code for users friends
                            }
                        })
                );



                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                    }
                });

                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        Intent intent = new Intent(MainActivity.this,SplashScreen.class);
                        startActivity(intent);
                        // Application code for when the batch finishes
                    }
                });
                batch.executeAsync();



                /*Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();*/
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }


    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*loginButton.setVisibility(View.VISIBLE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    /*public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }*/

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        Log.i(TAG,"isMyServiceRunning():: Entered");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG,"isMyServiceRunning():: TRUE-Service running");
                return true;
            }
        }
        Log.i(TAG, "isMyServiceRunning():: FALSE-Service NOT-running");
        return false;
    }
}
