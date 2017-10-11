package com.amigos.sachin.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amigos.sachin.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.firebase.client.ChildEventListener;
//import com.firebase.client.DataSnapshot;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
/*import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;*/
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

public class MyProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    private String TAG = "MyInfoFragment";
    private Uri filePath;
    String imageUrl = "";
    String myId = "";
    Context context;
    ImageView imageView,profilePhotoEdit;
    EditText et_name, et_age,et_place,et_status,et_activity1,et_activity2,et_activity3;
    Button okButton;
    public RadioGroup radioSexGroup;
    public RadioButton radioSexButton;
    View view;
    SharedPreferences sp;
    public static StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        context = getApplicationContext();
        et_name = (EditText)findViewById(R.id.et_display_name1);
        et_age = (EditText) findViewById(R.id.age1);
        et_place = (EditText) findViewById(R.id.place1);
        et_status = (EditText) findViewById(R.id.editText_status1);
        et_activity1 = (EditText) findViewById(R.id.editText_act11);
        et_activity2 = (EditText) findViewById(R.id.editText_act21);
        /*et_activity3 = (EditText) findViewById(R.id.editText_act31);*/
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex1);
        okButton = (Button) findViewById(R.id.button1);
        imageView = (ImageView) findViewById(R.id.iv_profile_pic1);
        profilePhotoEdit = (ImageView) findViewById(R.id.profilePhotoEdit1);
        sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId", "");

        fillData();
    }

    public void fillData(){

        Firebase.setAndroidContext(context);

        Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded");
                Log.i(TAG, "" + dataSnapshot.getValue());

                if ("name".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_name.setText(dataSnapshot.getValue().toString());
                }
                if ("age".equalsIgnoreCase(dataSnapshot.getKey())) {
                    if(isNumeric(dataSnapshot.getValue().toString().trim())) {
                        et_age.setText(dataSnapshot.getValue().toString().trim());
                    }else{
                        et_age.setText("");
                    }
                }
                if ("place".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_place.setText(dataSnapshot.getValue().toString());
                }
                if ("status".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_status.setText(dataSnapshot.getValue().toString());
                }
                if ("activity".equalsIgnoreCase(dataSnapshot.getKey())) {
                    for (DataSnapshot children : dataSnapshot.getChildren()){
                        if("act1".equalsIgnoreCase(children.getKey())){
                            et_activity1.setText(children.getValue().toString());
                        }
                        if("act2".equalsIgnoreCase(children.getKey())){
                            et_activity2.setText(children.getValue().toString());
                        }
                        /*if("act3".equalsIgnoreCase(children.getKey())){
                            et_activity3.setText(children.getValue().toString());
                        }*/
                    }
                }
                if ("sex".equalsIgnoreCase(dataSnapshot.getKey())) {
                    if("male".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioMale1);
                    }else if("female".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioFemale1);
                    }
                }
                if ("imageUrl".equalsIgnoreCase(dataSnapshot.getKey())){
                    for(DataSnapshot children : dataSnapshot.getChildren()){
                        if(myId.equalsIgnoreCase(children.getKey())){
                            imageUrl = children.getValue().toString();
                            /*Glide.with(context.getApplicationContext())
                                    .load(imageUrl)   //passing your url to load image.
                                    .override(18, 18)  //just set override like this
                                    .error(R.drawable.ic_user)
                                    .listener(glide_callback)
                                    .animate(R.anim.rotate_backward)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);*/
                            /*Glide.with(context)
                                    .load(imageUrl)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                            // Do something with bitmap here.
                                            imageView.setImageBitmap(bitmap);
                                            Log.e("GalleryAdapter","Glide getMedium ");

                                            Glide.with(context)
                                                    .load(imageUrl)
                                                    .asBitmap()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(new SimpleTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                                            // Do something with bitmap here.
                                                            imageView.setImageBitmap(bitmap);
                                                            Log.e("GalleryAdapter","Glide getLarge ");
                                                        }
                                                    });
                                        }
                                    });*/


                            Glide.with(context).load(imageUrl)
                                    .bitmapTransform(new CropSquareTransformation(context),
                                            new CropCircleTransformation(context))
                                    .thumbnail(0.01f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        });

        /*DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseRef.child("users").child(myId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded");
                Log.i(TAG, "" + dataSnapshot.getValue());

                if ("name".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_name.setText(dataSnapshot.getValue().toString());
                }
                if ("age".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_age.setText(dataSnapshot.getValue().toString());
                }
                if ("place".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_place.setText(dataSnapshot.getValue().toString());
                }
                if ("status".equalsIgnoreCase(dataSnapshot.getKey())) {
                    et_status.setText(dataSnapshot.getValue().toString());
                }
                if ("activity".equalsIgnoreCase(dataSnapshot.getKey())) {
                    for (DataSnapshot children : dataSnapshot.getChildren()){
                        if("act1".equalsIgnoreCase(children.getKey())){
                            et_activity1.setText(children.getValue().toString());
                        }
                        if("act2".equalsIgnoreCase(children.getKey())){
                            et_activity2.setText(children.getValue().toString());
                        }
                        *//*if("act3".equalsIgnoreCase(children.getKey())){
                            et_activity3.setText(children.getValue().toString());
                        }*//*
                    }
                }
                if ("sex".equalsIgnoreCase(dataSnapshot.getKey())) {
                    if("male".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioMale1);
                    }else if("female".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioFemale1);
                    }
                }
                if ("imageUrl".equalsIgnoreCase(dataSnapshot.getKey())){
                    for(DataSnapshot children : dataSnapshot.getChildren()){
                        if(myId.equalsIgnoreCase(children.getKey())){
                            imageUrl = children.getValue().toString();
                            Glide.with(context).load(imageUrl)
                                    .bitmapTransform(new CropSquareTransformation(context),
                                            new CropCircleTransformation(context))
                                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        updateData();
    }

    public void updateData(){
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) findViewById(selectedId);
                String sexStr = radioSexButton.getText().toString();

                Firebase myRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/");
                myRef.child("name").setValue(et_name.getText().toString());
                myRef.child("age").setValue(et_age.getText().toString());
                myRef.child("place").setValue(et_place.getText().toString());
                myRef.child("status").setValue(et_status.getText().toString());
                myRef.child("sex").setValue(sexStr);
                myRef.child("activity").child("act1").setValue(et_activity1.getText().toString());
                myRef.child("activity").child("act2").setValue(et_activity2.getText().toString());
                /*myRef.child("activity").child("act3").setValue(et_activity3.getText().toString());*/

                Toast.makeText(context,"Data updated successfully.",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MyProfileActivity.this,InterestsTagsActivity.class);
                startActivity(intent);
            }
        });

        profilePhotoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(v);
            }
        });

        //uploadFile();
    }

    public void showFileChooser(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

           /*requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);*/
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        } else {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                bitmap = getSquareCroppedBitmap(bitmap);
                bitmap = getCircularCroppedBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                        bitmap = getSquareCroppedBitmap(bitmap);
                        bitmap = getCircularCroppedBitmap(bitmap);
                        imageView.setImageBitmap(bitmap);


                        //imageView.setImageURI(data.getData());
                        Log.i(TAG, "image set");
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } else {


                    // Permission denied - Show a message to inform the user that this app only works
                    // with these permissions granted

                }
                return;
            }

        }
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static Bitmap getCircularCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    public static Bitmap getSquareCroppedBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = Math.min(width,height);
        int crop = Math.abs(width - height) / 2;
        Bitmap croppedBitmap;
        try {
            if (width > height) {
                croppedBitmap = Bitmap.createBitmap(bitmap, crop, 0, length, length);
            } else {
                croppedBitmap = Bitmap.createBitmap(bitmap, 0, crop, length, length);
            }
            return croppedBitmap;
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "inside onActivityResult");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.i(TAG, "inside filepath::" + filePath);
            checkPermissions();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        try {
            //if there is a file to upload
            if (filePath != null) {
                //displaying a progress dialog while upload is going on

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

                                //and displaying a success toast
                                //Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();
                                addUrlToProfile();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog

                                //and displaying error message
                                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
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
}
