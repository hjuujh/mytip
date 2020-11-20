package com.example.mytip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {
    private String uid, key, type;
    private boolean me;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private StorageReference sr;
    private FirebaseStorage fs;
    private BottomNavigationView navigation;
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

    String rtitle, rdate, rplace, rseat, rreview;
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");
        key = intent.getExtras().getString("key");
        me = intent.getExtras().getBoolean("me");
        type = intent.getExtras().getString("type");

        if(me){
            navigation = (BottomNavigationView) findViewById(R.id.navigation_review);
            navigation.setOnNavigationItemSelectedListener(myOnNavigationItemSelectedListener);
        }
        else{
            navigation = (BottomNavigationView) findViewById(R.id.navigation_review);
            navigation.setOnNavigationItemSelectedListener(otherOnNavigationItemSelectedListener);
        }

        fs = FirebaseStorage.getInstance();
        sr = fs.getReference().child(type+"/" + key);
        sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(getApplicationContext())
                        .load(imgUri=task.getResult())
                        .into(imgview);
            }
        });

        db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .collection(type).document(key)
                .get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            titleview.setText(rtitle=task.getResult().get("title").toString());
                            dateview.setText(rdate=task.getResult().get("date").toString());
                            placeview.setText(rplace=task.getResult().get("place").toString());
                            seatview.setText(rseat=task.getResult().get("seat").toString());
                            reviewview.setText(rreview=task.getResult().get("review").toString());
                        }
                        else{
                            System.out.println("###########");
                            System.out.println("fail");
                        }
                    }
                });

        navigation.getMenu().setGroupCheckable(0, false, true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener myOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cancel:
                    finish();
                    return true;
                case R.id.modify:
//                   수정 액티비티로 이동
                    Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                    intent.putExtra("title", rtitle);
                    intent.putExtra("place", rplace);
                    intent.putExtra("date", rdate);
                    intent.putExtra("seat", rseat);
                    intent.putExtra("review", rreview);
                    intent.putExtra("key", key);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    return true;
                case R.id.delete:
//                    경고창으로 확인받고 db에서 데이터, 이미지 삭제
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                    builder.setTitle("삭제 확인").setMessage("삭제하시겠습니까?");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            db.collection("users").document(uid)
                                    .collection(type).document(key)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                                }
                            });

                            db.collection(type+" reviews").document(key)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                                }
                            });

                            fs.getReference().child(type+"/" + key)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                            Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                            startActivity(intent);
//                            finish();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
//                            Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                    return true;
            }
            return false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener otherOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cancel:
                    finish();
                    return true;
                case R.id.modify:
                    Toast.makeText(getApplicationContext(), "수정할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.delete:
                    Toast.makeText(getApplicationContext(), "삭제할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return true;
            }
            return false;
        }
    };

}
