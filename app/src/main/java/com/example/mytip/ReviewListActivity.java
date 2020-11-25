package com.example.mytip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity {
    private String uid, id;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private ReviewListAdapter adapter;
    private ArrayList<ReviewItem> list;
    private Intent intent;
    private boolean me;
    private BottomNavigationView navigation;
    private Spinner reviewSpinner;
    private EditText editTextFilter;
    private RecyclerView recyclerView;
    private Context context;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String type, selected;
    private int p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        context = this;
        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        intent = getIntent();

        if(!TextUtils.isEmpty(intent.getStringExtra("id"))){
            id = intent.getStringExtra("id");
            uid = id;
            me = false;
        }
        else{
            uid = user.getUid();
            me = true;
        }
        if(!TextUtils.isEmpty(intent.getStringExtra("type"))){
            type = intent.getStringExtra("type");
        }
        else {
            type = "performance";
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.review_list);
        setMenu();

        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this, R.array.review_search, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        reviewSpinner = (Spinner)findViewById(R.id.spinner);
        reviewSpinner.setAdapter(userAdapter);
        reviewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected = reviewSpinner.getItemAtPosition(position).toString();
                adapter = new ReviewListAdapter(list, me, uid, selected, type);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        editTextFilter = (EditText)findViewById(R.id.search);
        editTextFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }
        });

    }

    private void setMenu(){
        if(me){
            navigation = (BottomNavigationView) findViewById(R.id.my_list);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(myOnNavigationItemSelectedListener);
            if(type.equals("performance")){
                navigation.setSelectedItemId(R.id.performance);
            }
            else if(type.equals("movie")){
                navigation.setSelectedItemId(R.id.movie);
            }
        }
        else{
            navigation = (BottomNavigationView) findViewById(R.id.other_list);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(otherOnNavigationItemSelectedListener);
            if(type.equals("performance")){
                navigation.setSelectedItemId(R.id.performance);
            }
            else if(type.equals("movie")){
                navigation.setSelectedItemId(R.id.movie);
            }
        }
    }

    public void addItem(String title, String date, String time, String key, String show, String review) {
        ReviewItem item = new ReviewItem();

        item.setTitle(title);
        item.setDate(date);
        item.setTime(time);
        item.setKey(key);
        item.setShow(show);
        item.setReview(review);

        list.add(item);
    }

    private void getList(String type){
        list = new ArrayList<>();
        db.collection("users").document(uid).collection(type)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                addItem(document.getData().get("title").toString(), document.getData().get("date").toString()
                                        ,document.getData().get("time").toString(),document.getId()
                                        ,document.getData().get("show").toString(),document.getData().get("review").toString());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
//        adapter = new ReviewListAdapter(list, me, uid, "제목",type);
        if(TextUtils.isEmpty(selected) || selected.equals("제목")){
            adapter = new ReviewListAdapter(list, me, uid, "제목",type);
            recyclerView.setAdapter(adapter);
        }
        else if(selected.equals("리뷰 내용")){
            adapter = new ReviewListAdapter(list, me, uid, "리뷰 내용",type);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener myOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add:
                    intent = new Intent(getApplicationContext(), TicketActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.performance:
                    type = "performance";
                    getList(type);
                    return true;
                case R.id.movie:
                    type = "movie";
                    getList(type);
                    return true;
                case R.id.others:
                    intent = new Intent(getApplicationContext(), UserListActivity.class);
                    startActivity(intent);
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
                case R.id.add:
                    return true;
                case R.id.performance:
                    type = "performance";
                    getList(type);
                    return true;
                case R.id.movie:
                    type = "movie";
                    getList(type);
                    return true;
                case R.id.back:
                    finish();
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
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.account:
                intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                //select back button
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}