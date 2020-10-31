package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String uid;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        ListView listview;
        UserListAdapter adapter;

        adapter = new UserListAdapter() ;

        listview = (ListView) findViewById(R.id.userview);
        listview.setAdapter(adapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.user_list);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.others);

        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(uid.equals(document.getData().get("uid").toString())){

                                }
                                else {
                                    adapter.addItem(document.getData().get("name").toString(),document.getData().get("email").toString(),document.getData().get("uid").toString());
                                    listview.setAdapter(adapter);
                                }
                            }
                        } else {
                            System.out.println(task.getException());
                        }
                    }
                });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                UserItem item = (UserItem) parent.getItemAtPosition(position);
                String uid = item.getUid();
                Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                intent.putExtra("id",uid);
                startActivity(intent);
            }
        }) ;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add:
//                    intent = new Intent(getApplicationContext(), TicketActivity.class);
//                    startActivity(intent);
                    return true;
                case R.id.performance:
                    intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.movie:
//                    영화리스트 액티비티 추가
                    return true;
                case R.id.others:
                    return true;
            }
            return false;
        }
    };

}