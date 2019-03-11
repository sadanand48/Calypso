package com.example.vaibhav.mosahajya;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    static final int REQUEST_VIDEO_CAPTURE = 1;
    Button register,videobtn,videobtn2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    private Uri videoUri;
    private StorageReference videoRef;
    private static final int REQUEST_CODE = 101;
    EditText phone;
    private StorageReference mStorageRef;
    HashMap<String,String> hm;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=(Button)findViewById(R.id.submit);
        videobtn=(Button)findViewById(R.id.buttonr);
        videobtn2=(Button)findViewById(R.id.buttonr2);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();
        videoRef = storageRef.child("/videos");


        hm=new HashMap<String, String>();
        firebaseDatabase=FirebaseDatabase.getInstance();
       reference=firebaseDatabase.getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Send Alerts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        videobtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ph="887565876687";
                FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Log.d("success", "Successful");
                                Toast.makeText(RegisterActivity.this, "successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( RegisterActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String mToken = instanceIdResult.getToken();
                        Log.e("Token",mToken);
                        hm.put(ph,mToken);
                        reference.child("Emergency Contacts").push().setValue(hm);
                        Toast.makeText(getApplicationContext(),"Alert Sent" ,Toast.LENGTH_LONG).show();
                        finish();

                    }
                });

            }
        });
    }
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        videoUri = data.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" +
                        videoUri, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_CODE);
        }
    }
    public void upload() {
        if (videoUri != null) {
            UploadTask uploadTask = videoRef.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this,
                            "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterActivity.this, "Upload complete",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }
}
