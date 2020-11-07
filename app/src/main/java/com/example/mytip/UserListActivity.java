package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
    private ListView userListView;
    private UserListAdapter userListAdapter;
    private ListView reviewListView;
    private ReviewListAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        Spinner userSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this, R.array.user_search, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.user_list);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.others);

        EditText editTextFilter = (EditText)findViewById(R.id.search);
        userListAdapter = new UserListAdapter();
        userListView = (ListView) findViewById(R.id.userview);
        reviewListView = (ListView) findViewById(R.id.reviewview);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selected = userSpinner.getItemAtPosition(position).toString();
                if (selected.equals("사용자")) {
                    userListView.setVisibility(View.VISIBLE);
                    reviewListView.setVisibility(View.GONE);
                    userListView.setAdapter(userListAdapter);
                    db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (!uid.equals(document.getData().get("uid").toString())) {
                                                userListAdapter.addItem(document.getData().get("name").toString(), document.getData().get("email").toString(), document.getData().get("uid").toString());
                                                userListView.setAdapter(userListAdapter);
                                            }
                                        }
                                    } else {
                                        System.out.println(task.getException());
                                    }
                                }
                            });

                    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View v, int position, long id) {

                            UserItem item = (UserItem) parent.getItemAtPosition(position);
                            String uid = item.getUid();
                            Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                            intent.putExtra("id", uid);
                            startActivity(intent);
                        }
                    });

                    editTextFilter.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {

                            String filterText = edit.toString();
                            ((UserListAdapter) userListView.getAdapter()).getFilter().filter(filterText);
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });
                }
                else {
                    if (selected.equals("공연 제목")) {
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        reviewAdapter = new ReviewListAdapter(1);
                        reviewListView.setAdapter(reviewAdapter);
                    } else if (selected.equals("영화 제목")) {
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        reviewAdapter = new ReviewListAdapter(2);
                        reviewListView.setAdapter(reviewAdapter);
                    } else if (selected.equals("리뷰 내용")) {
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        reviewAdapter = new ReviewListAdapter(3);
                        reviewListView.setAdapter(reviewAdapter);
                    }
                    db = FirebaseFirestore.getInstance();
                    db.collection("reviews")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if ((Boolean) document.getData().get("show")) {
                                                reviewAdapter.addItem(document.getData().get("uname").toString(), document.getData().get("date").toString(), document.getData().get("title").toString(), document.getData().get("uid").toString(), document.getData().get("review").toString());
                                                reviewListView.setAdapter(reviewAdapter);
                                            }
                                        }
                                    } else {
                                        System.out.println(task.getException());
                                    }
                                }
                            });

                    reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View v, int position, long id) {
                            ReviewItem item = (ReviewItem) parent.getItemAtPosition(position);
                            Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                            intent.putExtra("uid",item.getUid());
                            System.out.println(item.getUid());
                            intent.putExtra("title",item.getTitle());
                            System.out.println(item.getTitle());
                            intent.putExtra("date",item.getDate());
                            System.out.println(item.getDate());
                            intent.putExtra("me",false);
                            startActivity(intent);
                        }
                    });

                    editTextFilter.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {
                            String filterText = edit.toString();
                            ((ReviewListAdapter) reviewListView.getAdapter()).getFilter().filter(filterText);
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add:
                    intent = new Intent(getApplicationContext(), TicketActivity.class);
                    startActivity(intent);
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