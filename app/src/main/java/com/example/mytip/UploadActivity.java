package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button btn;
    private FirebaseAuth firebaseAuth;
    private Map<String, Object> data;


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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title=ttitle.getText().toString();
                place=tplace.getText().toString();
                date=tdate.getText().toString();
                seat=tseat.getText().toString();
                review = treview.getText().toString();

                dataSet();
                urlSet();
                upLoad();


                Intent intent = new Intent(getApplicationContext(),ReviewListActivity.class);
                intent.putExtra("id",uid);
                startActivity(intent);
            }
        });
    }

    private void urlSet() {

        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReference().child(uid + "/performance/" + title+date);
//        String url = sr.getDownloadUrl().addOnCompleteListener(this, new );

        System.out.println(")))))))))");
//        System.out.println(url);

        data.put("img", img);

    }

    private void dataSet() {
        data = new HashMap<>();
        //review = "후기후기후기후기";
        long now = System.currentTimeMillis();
        Date d = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = mFormat.format(d);
        String key = title+date;

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

    private void upLoad() {
        final String TAG = "";
        System.out.println("&&&&&&&&&&&&&&&&");
        System.out.println(img);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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