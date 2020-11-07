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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    private TextView ttitle, tplace, tdate, tseat, treview;
    private String uid, title, place, date, seat, review, img;
    private Uri imgUri;
    private Button btn;
    private FirebaseAuth firebaseAuth;
    private Map<String, Object> data;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private StorageReference sr;
    private Boolean newticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        title = getIntent().getStringExtra("title");
        place = getIntent().getStringExtra("place");
        date = getIntent().getStringExtra("date");
        seat = getIntent().getStringExtra("seat");
        review = getIntent().getStringExtra("review");
        newticket = getIntent().getBooleanExtra("newticket",false);
        if(newticket) {
            img = getIntent().getStringExtra("imgUri");
            imgUri = Uri.parse(img);
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

        if(!newticket) {//새로운티켓이 아닐때는 수정불가
            ttitle.setFocusable(false);
            tdate.setFocusable(false);
        }

        btn.setOnClickListener(view -> {//디비에 넣는 시간 벌기 +  제목명과 일시는 못바꾼다고 한번 더 확인
            title=ttitle.getText().toString();
            place=tplace.getText().toString();
            date=tdate.getText().toString();
            seat=tseat.getText().toString();
            review = treview.getText().toString();
            dataSet();
            Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
            if(newticket) {
                imgUpload();
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                builder
                        .setMessage("공연명과 일시는 바꿀 수 없습니다. 공연명 [" + title + "] 일시 [" + date + "]가 맞습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    upLoad();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("아니요", (dialog, which) -> dialog.cancel());
                builder.create().show();
            }else{
                try {
                    upLoad();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

    }

    private void dataSet() {
        data = new HashMap<>();
        long now = System.currentTimeMillis();
        Date d = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = mFormat.format(d);
        //String key = title+date;

        System.out.println("###############");
        System.out.println(img);

        data.put("title", title);
        data.put("date", date);
        data.put("review", review);
        data.put("seat", seat);
        data.put("place", place);
        data.put("time", time);
        data.put("show",true);
    }

    private void imgUpload() {
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        StorageReference imgRef= firebaseStorage.getReference(uid+"/performance/"+title+date);

        imgRef.putFile(imgUri);
        System.out.println("이미지 업로드 성공");
    }

    private void upLoad() throws InterruptedException {
        final String TAG = "";

        db = FirebaseFirestore.getInstance();

        try{
            docRef = db.collection("users").document(uid)
                    .collection("performance").document(title+date);

            docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
        catch (Exception e){
        }
        finally {
            db.collection("users").document(uid)
                    .collection("performance").document(title+date)
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
        }
    }
}