package com.amigos.sachin.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.amigos.sachin.MyProfileFragments.MyInfoFragment;
import com.amigos.sachin.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sachin on 10/8/2017.
 */

public class UploadService extends Service {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    String myId = "";
    Context context;
    Uri filePath;
    public static StorageReference storageReference;
    private static int NOTIFY_ID=1337;
    private static int FOREGROUND_ID=1338;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        Firebase.setAndroidContext(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        filePath = (Uri) intent.getExtras().get("filePath");

        startForeground(FOREGROUND_ID,
                buildForegroundNotification());

        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        uploadFile();

        /*new LongOperation().execute("");*/

        return START_STICKY;
    }

    private void uploadFile() {
        try {
            //if there is a file to upload
            if (filePath != null) {
                //displaying a progress dialog while upload is going on
                /*final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Uploading");
                progressDialog.show();*/
                MyInfoFragment.progressBar.setVisibility(View.VISIBLE);

                StorageReference riversRef = storageReference.child("images/" + myId+"/"+myId + ".jpg");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

                byte[] byteArray = out.toByteArray();


                //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                riversRef.putBytes(byteArray)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                /*progressDialog.dismiss();*/

                                //and displaying a success toast
                                //Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();
                                String s = "";
                                addUrlToProfile();
                                MyInfoFragment.progressBar.setVisibility(View.GONE);
                                stopForeground(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                /*progressDialog.dismiss();*/
                                MyInfoFragment.progressBar.setVisibility(View.GONE);

                                //and displaying error message
                                /*Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();*/
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();


                                //displaying percentage in progress dialog
                                /*progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");*/
                            }
                        });
            }
            //if there is not any file
            else {
                //Toast.makeText(context,"File not found!!!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addUrlToProfile(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("images/"+ myId+ "/" + myId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Firebase myImageUrlRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/imageUrl/");
                    myImageUrlRef.child(myId).setValue((new URL(uri.toString())).toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true)
                .setContentTitle("Amigos")
                .setContentText("Uploading image")
                .setSmallIcon(R.drawable.logo);

        return(b.build());
    }

    /*private void raiseNotification(Intent inbound, File output,
                                   Exception e) {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());

        if (e == null) {
            b.setContentTitle(getString(R.string.download_complete))
                    .setContentText(getString(R.string.fun))
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setTicker(getString(R.string.download_complete));

            Intent outbound=new Intent(Intent.ACTION_VIEW);
            Uri outputUri=
                    FileProvider.getUriForFile(this, AUTHORITY, output);

            outbound.setDataAndType(outputUri, inbound.getType());
            outbound.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PendingIntent pi=PendingIntent.getActivity(this, 0,
                    outbound, PendingIntent.FLAG_UPDATE_CURRENT);

            b.setContentIntent(pi);
        }
        else {
            b.setContentTitle(getString(R.string.exception))
                    .setContentText(e.getMessage())
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setTicker(getString(R.string.exception));
        }

        NotificationManager mgr=
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mgr.notify(NOTIFY_ID, b.build());
    }*/

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String

            uploadFile();

            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {

            addUrlToProfile();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
