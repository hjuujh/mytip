package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    private TextView ttitle, tplace, tdate, tseat, treview;
    private String uid, uname, title, place, date, seat, review, img;
    private Uri imgUri;
    private Button btn;
    private FirebaseAuth firebaseAuth;
    private Map<String, Object> data, allReviews;
    private FirebaseFirestore db;
    private Boolean newticket;
    private String key;
    private int kind;
    private Intent intent;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        uname = user.getDisplayName();
        db = FirebaseFirestore.getInstance();

        title = getIntent().getStringExtra("title");
        place = getIntent().getStringExtra("place");
        date = getIntent().getStringExtra("date");
        seat = getIntent().getStringExtra("seat");
        review = getIntent().getStringExtra("review");
        type = getIntent().getStringExtra("type");

        newticket = getIntent().getBooleanExtra("newticket",false);
        if(newticket) {
            img = getIntent().getStringExtra("imgUri");
            imgUri = Uri.parse(img);
            key = db.collection(type+" reviews").document().getId();
        }
        else {
            key = getIntent().getStringExtra("key");
        }

        Click();
    }

    private void Click(){
        ttitle = findViewById(R.id.title);
        ttitle.setText(title);
        tplace = findViewById(R.id.place);
        tplace.setText(place);
        tdate = findViewById(R.id.date);
        tdate.setText(date);
        tseat = findViewById(R.id.seat);
        tseat.setText(seat);
        treview = findViewById(R.id.review);
        treview.setText(review);
        btn = findViewById(R.id.btn);

        btn.setOnClickListener(view -> {
            title=ttitle.getText().toString();
            place=tplace.getText().toString();
            date=tdate.getText().toString();
            seat=tseat.getText().toString();
            review = treview.getText().toString();
            dataSet();
            intent = new Intent(getApplicationContext(), ReviewListActivity.class);
            if(newticket){
                upLoad();
            }
            else {
                modify();
            }
        });
    }

    private void modify() {
        final String TAG = "";

        db.collection(type+" reviews").document(key)
                .set(allReviews)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG,"DocumentSnapshot successfully written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        db.collection("users").document(uid)
                .collection(type).document(key)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"DocumentSnapshot successfully written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        startActivity(intent);
    }

    private void dataSet() {
        data = new HashMap<>();
        allReviews = new HashMap<>();
        long now = System.currentTimeMillis();
        Date d = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = mFormat.format(d);

        data.put("title", title);
        data.put("date", date);
        data.put("review", review);
        data.put("seat", seat);
        data.put("place", place);
        data.put("time", time);
        data.put("show",true);

        allReviews.put("uid", uid);
        allReviews.put("uname", uname);
        allReviews.put("title", title);
        allReviews.put("date", date);
        allReviews.put("review", review);
        allReviews.put("show",true);
    }

    private void upLoad() {
        final String TAG = "";

        StorageReference storage = FirebaseStorage.getInstance()
                .getReference()
                .child(type+"/"+key);
        storage.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                db.collection(type+" reviews").document(key)
                        .set(allReviews)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d(TAG,"DocumentSnapshot successfully written");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                db.collection("users").document(uid)
                        .collection(type).document(key)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG,"DocumentSnapshot successfully written");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                startActivity(intent);
            }
        });
    }
}