package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class ReviewActivity extends AppCompatActivity {
    private Button btn;
    private String uid, title, date;
    private Context context;
    private FirebaseAuth firebaseAuth;
    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        ListView listview;
        ListViewAdapter adapter;
        btn = findViewById(R.id.getdata);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TicketActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

//        ImageView img = findViewById(R.id.img);

        adapter = new ListViewAdapter(ReviewActivity.this) ;

        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("users").document(id).collection("performance").document();
        CollectionReference colRef = db.collection("users").document(uid).collection("performance");

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            final String TAG = "";

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();

                    for (DocumentSnapshot q : query){
//                        doc = (DocumentSnapshot) q.getData();
                        title = (String) q.getData().get("title");
                        date = (String) q.getData().get("date");
                        String key = title+date;
                        System.out.println(key);

                        FirebaseStorage storage= FirebaseStorage.getInstance("gs://mytip-22034.appspot.com/");
                        StorageReference ref= storage.getReference();

                        ref.child(uid+"/performance/"+ key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println("##############1");
                                System.out.println(uri);
                                adapter.addItem(uri, title, date);
                                listview.setAdapter(adapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                       listview.setAdapter(adapter);
                    }
//
//                    if (doc.getDocuments()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
//                        System.out.println(doc.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                        System.out.println("No such document");
//                    }
                } else {
                    Log.d(TAG, "Get failed with ", task.getException());
                    System.out.println("Get failed with " + task.getException());
                }
            }
        });

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView parent, View v, int position, long id) {
//                // get item
//                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;
//            }
//        }) ;

    }

}
