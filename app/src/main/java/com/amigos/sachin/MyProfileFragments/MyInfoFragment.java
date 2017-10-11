package com.amigos.sachin.MyProfileFragments;


import android.Manifest;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amigos.sachin.Activities.SettingsActivity;
import com.amigos.sachin.R;
import com.amigos.sachin.Services.UploadService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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

import static android.app.Activity.RESULT_OK;


public class MyInfoFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 234;
    private String TAG = "MyInfoFragment";
    private Uri filePath;
    String imageUrl = "";
    String myId = "";
    Context context;
    ImageView imageView,profilePhotoEdit;
    RelativeLayout settingsIcon;
    EditText et_name, et_age, et_place, et_status, et_activity1, et_activity2, et_activity3;
    Button okButton;
    public RadioGroup radioSexGroup;
    public RadioButton radioSexButton;
    View view;
    SharedPreferences sp;
    public static StorageReference storageReference;
    public static ProgressBar progressBar;

    public MyInfoFragment() {

    }

    public static MyInfoFragment newInstance(String param1) {
        MyInfoFragment fragment = new MyInfoFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_my_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRetainInstance(true);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        context = getActivity();
        Firebase.setAndroidContext(context);

        et_name = (EditText) view.findViewById(R.id.et_display_name);
        et_age = (EditText) view.findViewById(R.id.age);
        et_place = (EditText) view.findViewById(R.id.place);
        et_status = (EditText) view.findViewById(R.id.editText_status);
        et_activity1 = (EditText) view.findViewById(R.id.editText_act1);
        et_activity2 = (EditText) view.findViewById(R.id.editText_act2);
        /*et_activity3 = (EditText) view.findViewById(R.id.editText_act3);*/
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSex);
        okButton = (Button) view.findViewById(R.id.button);
        imageView = (ImageView) view.findViewById(R.id.iv_profile_pic);
        profilePhotoEdit = (ImageView) view.findViewById(R.id.profilePhotoEdit);
        settingsIcon = (RelativeLayout) view.findViewById(R.id.layoutSettings);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_image);
        sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId", "");

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });

        fillData();
    }

    public void fillData(){
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
                        radioSexGroup.check(R.id.radioMale);
                    }else if("female".equalsIgnoreCase(dataSnapshot.getValue().toString())){
                        radioSexGroup.check(R.id.radioFemale);
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        updateData();
    }

    public void updateData(){
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) view.findViewById(selectedId);
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

                Toast.makeText(context,"Data updated successfully",Toast.LENGTH_LONG).show();
            }
        });

        profilePhotoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(v);
            }
        });

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

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    public void showFileChooser(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
            requestPermissions(new String[]{
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
                Intent intent = new Intent(context, UploadService.class);
                intent.putExtra("filePath",filePath);
                context.startService(intent);
                /*AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile();
                    }
                });*/
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
                /*final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Uploading");
                progressDialog.show();*/

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
                                addUrlToProfile();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                /*progressDialog.dismiss();*/

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

}
