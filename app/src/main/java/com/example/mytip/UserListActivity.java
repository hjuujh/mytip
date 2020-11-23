package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
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
    private SearchListAdapter searchAdapter;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner userSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this, R.array.user_search, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.user_list);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.others);

        EditText editTextFilter = (EditText)findViewById(R.id.search);
        userListView = (ListView) findViewById(R.id.userview);
        reviewListView = (ListView) findViewById(R.id.reviewview);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selected = userSpinner.getItemAtPosition(position).toString();
                if (selected.equals("사용자")) {
                    userListView.setVisibility(View.VISIBLE);
                    reviewListView.setVisibility(View.GONE);
                    userListAdapter = new UserListAdapter();
                    userListView.setAdapter(userListAdapter);

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
                            Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                            intent.putExtra("id", item.getUid());
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
                        type = "performance";
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchListAdapter(1, type);
                        reviewListView.setAdapter(searchAdapter);
                    } else if (selected.equals("영화 제목")) {
                        type = "movie";
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchListAdapter(1, type);
                        reviewListView.setAdapter(searchAdapter);
                    } else if (selected.equals("공연 리뷰 내용")) {
                        type = "performance";
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchListAdapter(3, type);
                        reviewListView.setAdapter(searchAdapter);
                    } else if (selected.equals("영화 리뷰 내용")) {
                        type = "movie";
                        userListView.setVisibility(View.GONE);
                        reviewListView.setVisibility(View.VISIBLE);
                        searchAdapter = new SearchListAdapter(3, type);
                        reviewListView.setAdapter(searchAdapter);
                    }

                    db.collection(type + " reviews")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if ((Boolean) document.getData().get("show")) {
                                                searchAdapter.addItem(document.getData().get("uname").toString(), document.getData().get("date").toString(),
                                                        document.getData().get("title").toString(), document.getData().get("uid").toString(),
                                                        document.getData().get("review").toString(), document.getData().get("show").toString(), document.getId());
                                                reviewListView.setAdapter(searchAdapter);
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
                            SearchItem item = (SearchItem) parent.getItemAtPosition(position);
                            Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                            intent.putExtra("uid",item.getUid());
                            intent.putExtra("key",item.getKey());
                            intent.putExtra("type",type);
                            intent.putExtra("me",false);
                            startActivity(intent);
                        }
                    });

                    editTextFilter.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {
                            String filterText = edit.toString();
                            ((SearchListAdapter) reviewListView.getAdapter()).getFilter().filter(filterText);
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
                    intent.putExtra("type","performance");
                    startActivity(intent);
                    return true;
                case R.id.movie:
                    intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                    intent.putExtra("type","movie");
                    startActivity(intent);
                    return true;
                case R.id.others:
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.account:
                //select account item
                break;
            case android.R.id.home:
                //select back button
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}