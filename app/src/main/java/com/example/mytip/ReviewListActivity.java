package com.example.mytip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private ArrayList<ReviewItem> list = new ArrayList<>();
    private Intent intent;
    private boolean me;
    private BottomNavigationView navigation;
    private Spinner reviewSpinner;
    private EditText editTextFilter;
    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        context = this;
        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        try {
            id = getIntent().getExtras().getString("id");
            uid = id;
            me = false;
        }
        catch (Exception e){
            uid = user.getUid();
            me = true;
        }

        recyclerView = (RecyclerView)findViewById(R.id.review_list);
        getReviewList();
        reviewSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this, R.array.review_search, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewSpinner.setAdapter(userAdapter);
        reviewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selected = reviewSpinner.getItemAtPosition(position).toString();
                if(selected.equals("제목")) {
                    adapter = new ReviewListAdapter(list, me, uid, 1);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
                else {
                    adapter = new ReviewListAdapter(list, me, uid, 2);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        editTextFilter = (EditText)findViewById(R.id.search);

        if(me){
            navigation = (BottomNavigationView) findViewById(R.id.my_list);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(myOnNavigationItemSelectedListener);
            navigation.setSelectedItemId(R.id.performance);
        }
        else{
            navigation = (BottomNavigationView) findViewById(R.id.other_list);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(otherOnNavigationItemSelectedListener);
            navigation.setSelectedItemId(R.id.performance);
        }

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

    public void addItem(String title, String date, String review, String seat, String time, String key, String show) {
        ReviewItem item = new ReviewItem();

        item.setTitle(title);
        item.setDate(date);
        item.setReview(review);
        item.setSeat(seat);
        item.setTime(time);
        item.setKey(key);
        item.setShow(show);

        list.add(item);
    }

    private void getReviewList(){

        db.collection("users").document(uid).collection("performance")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                addItem(document.getData().get("title").toString(), document.getData().get("date").toString()
                                ,document.getData().get("review").toString(),document.getData().get("seat").toString()
                                ,document.getData().get("time").toString(),document.getId()
                                ,document.getData().get("show").toString());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
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
                    return true;
                case R.id.movie:
//                    영화리스트 액티비티 추가
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
                    return true;
                case R.id.movie:
//                    영화리스트 액티비티 추가
                    return true;
                case R.id.back:
                    finish();
                    return true;
            }
            return false;
        }
    };

}