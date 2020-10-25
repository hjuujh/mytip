package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    private TextView ttitle, tplace, tdate, tseat;
    private String id, title, place, date, seat, review;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        place = getIntent().getStringExtra("place");
        date = getIntent().getStringExtra("date");
        seat = getIntent().getStringExtra("seat");

        ttitle = findViewById(R.id.title);
        ttitle.setText(title);
        tplace = findViewById(R.id.place);
        tplace.setText(place);
        tdate = findViewById(R.id.date);
        tdate.setText(date);
        tseat = findViewById(R.id.seat);
        tseat.setText(seat);
        btn = findViewById(R.id.btn);
        upLoad();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }

    private void upLoad() {
        final String TAG = "";

        Map<String, Object> data = new HashMap<>();
        review = "후기후기후기후기";
        long now = System.currentTimeMillis();
        Date d = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = mFormat.format(d);
        String key = title+date;

        data.put("title", title);
        data.put("date", date);
        data.put("review", review);
        data.put("seat", seat);
        data.put("time", time);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id)
                .collection("performance").document(key)
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