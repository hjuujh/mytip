package com.example.mytip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {
    private String uid, title, date;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    @BindView(R.id.title)
    TextView titleview;
    @BindView(R.id.date)
    TextView dateview;
    @BindView(R.id.place)
    TextView placeview;
    @BindView(R.id.seat)
    TextView seatview;
    @BindView(R.id.review)
    TextView reviewview;
    @BindView(R.id.img)
    ImageView imgview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        date = intent.getExtras().getString("date");

        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReference().child(uid + "/performance/" + title+date);
        sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(getApplicationContext())
                        .load(task.getResult())
                        .into(imgview);
            }
        });

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("performance").document(title+date);
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
//                    System.out.println("###########");
//                    System.out.println(task.getResult().get("title"));
                    titleview.setText(task.getResult().get("title").toString());
//                    System.out.println(task.getResult().get("date"));
                    dateview.setText(task.getResult().get("date").toString());
//                    System.out.println(task.getResult().get("place"));
                    placeview.setText(task.getResult().get("place").toString());
//                    System.out.println(task.getResult().get("seat"));
                    seatview.setText(task.getResult().get("seat").toString());
//                    System.out.println(task.getResult().get("review"));
                    reviewview.setText(task.getResult().get("review").toString());
                }
                else{
                    System.out.println("###########");
                    System.out.println("fail");
                }
            }
        });

    }
}
