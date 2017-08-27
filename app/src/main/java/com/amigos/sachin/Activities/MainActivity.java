package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigos.sachin.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;
    private String facebook_id,f_name, m_name, l_name, gender, profile_image, full_name, email_id;
    long fb_id;
    String profile_name;
    String birthday;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        loginButton = (LoginButton)findViewById(R.id.button_fb_login);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);
        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        if (isLoggedIn()){
            Intent intent = new Intent(MainActivity.this,MainTabsActivity.class);
            startActivity(intent);
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker profileTracker;
            @Override
            public void onSuccess(LoginResult loginResult) {
//                textView.setText("Login Success \n" + loginResult.getAccessToken().getUserId() + "\n" +
//                loginResult.getAccessToken().getToken());
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
                                if(currentProfile.getProfilePictureUri(400, 400) != null) {
                                    profile_image = currentProfile.getProfilePictureUri(400, 400).toString();
                                    new ImageLoadTask(currentProfile.getProfilePictureUri(400, 400).toString(), imageView).execute();
                                }
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
                    if(profile.getProfilePictureUri(400, 400) != null) {
                        profile_image = profile.getProfilePictureUri(400, 400).toString();
                        new ImageLoadTask(profile.getProfilePictureUri(400, 400).toString(), imageView).execute();
                    }
                }
                //profileTracker.startTracking();



                GraphRequest request = GraphRequest.newMeRequest(
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
                                    textView.setText("facebook_id: "+ facebook_id+
                                            "\nf_name: " + f_name+
                                            "\nm_name: " + m_name+
                                            "\nl_name: " + l_name+
                                            "\nfull_name: " + full_name+
                                            "\nemail_id: " + email_id+
                                            "\ngender: " + gender+
                                            "\nprofile_name: " + profile_name+
                                            "\nbirthday: " + birthday+
                                            "\nfb_id: " + fb_id);
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
                                        userRef.child("imageUrl").child(myId).setValue(profile_image);
                                    }
                                    if(birthday != null){
                                        userRef.child("birthday").setValue(birthday);
                                    }
                                    //Creating fake profiles
                                    /*for (int i = 0; i<25 ; i++){
                                        Firebase ref = new Firebase("https://new-amigos.firebaseio.com/users/test"+i+ "/");
                                        if(profile_name != null) {
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
                                        }
                                    }*/
                                    SharedPreferences sp=getApplicationContext().getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
                                    sp.edit().putString("myId",""+ myId).apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(MainActivity.this,SplashScreen.class);
                                startActivity(intent);

                            }
                        });



                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                textView.setText("Login Cancelled.");
            }

            @Override
            public void onError(FacebookException error) {
                textView.setText("Error.");
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

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

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
