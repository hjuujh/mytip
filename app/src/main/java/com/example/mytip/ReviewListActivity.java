package com.example.mytip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewListActivity extends AppCompatActivity {
    private String uid, id;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    @BindView(R.id.review_list)
    RecyclerView reviewList;
    private FirestoreRecyclerAdapter adapter;
    private Intent intent;
    private boolean me;
    private BottomNavigationView navigation;
    private Boolean checked;
    private final String TAG="";
    private Spinner reviewSpinner;
    private EditText editTextFilter;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        try {
            id = getIntent().getExtras().getString("id");
            uid = id;
            me = false;
        }
        catch (Exception e){
            uid = user.getUid();
            me = true;
        }

        ButterKnife.bind(this);
        init();
        reviewSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this, R.array.review_search, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewSpinner.setAdapter(userAdapter);

        editTextFilter = (EditText)findViewById(R.id.search);

        if(me){
            getMyReviewList(true);
            navigation = (BottomNavigationView) findViewById(R.id.my_list);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(myOnNavigationItemSelectedListener);
            navigation.setSelectedItemId(R.id.performance);
        }
        else{
            getOtherReviewList();
            navigation = (BottomNavigationView) findViewById(R.id.other_list);
            navigation.setVisibility(View.VISIBLE);
            navigation.setOnNavigationItemSelectedListener(otherOnNavigationItemSelectedListener);
            navigation.setSelectedItemId(R.id.performance);
        }

        editTextFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                if(me){
                    if(editTextFilter.getText().equals("")){getMyReviewList(true);}
                    else{getMyReviewList(false);}

                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

    private void init(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        reviewList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getMyReviewList(boolean isNull){

        System.out.println(isNull);
        if(isNull) {
            query = db.collection("users").document(uid).collection("performance")
                    .orderBy("time", Query.Direction.DESCENDING);
        }
        else{
            System.out.println(editTextFilter.getText());
            String search = editTextFilter.getText().toString().toUpperCase();
            query = db.collection("users").document(uid).collection("performance")
                    .orderBy("title")
                    .startAt(search).endAt(search + "\uf8ff");
        }
//        reviewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                String selected = reviewSpinner.getItemAtPosition(position).toString();
//                if (selected.equals("사용자")) {
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//

        FirestoreRecyclerOptions<ReviewList> response = new FirestoreRecyclerOptions.Builder<ReviewList>()
                .setQuery(query, ReviewList.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ReviewList, ReviewsHolder>(response) {
            @Override
            public void onBindViewHolder(ReviewsHolder holder, int position, ReviewList model) {

                FirebaseStorage fs = FirebaseStorage.getInstance();
                StorageReference sr = fs.getReference().child(uid + "/performance/" + model.getTitle()+model.getDate());
                sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Glide.with(holder.itemView)
                                .load(task.getResult())
                                .into(holder.imageView);
                    }
                });
                System.out.println("$$$$$$$$$$$$$$$$$$");
                holder.textTitle.setText(model.getTitle());
                System.out.println(model.getTitle());
                holder.textDate.setText(model.getDate());
                System.out.println(model.getDate());
                holder.chx.setChecked(model.getShow());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("title", model.getTitle());
                    intent.putExtra("date", model.getDate());
                    intent.putExtra("me", me);
                    startActivity(intent);
                });

                holder.chx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getShow()){
                            checked = false;
                        }
                        else {
                            checked = true;
                        }
                        DocumentReference docRef = db.collection("users").document(uid).collection("performance")
                                .document(model.getTitle()+model.getDate());
                        docRef.update("show", checked)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    db.collection("reviews").document((String) task.getResult().getData().get("reviewKey"))
                                            .update("show", checked)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            }
                        });

                    }
                });
            }

            @Override
            public ReviewsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.review_item, group, false);

                return new ReviewsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        reviewList.setAdapter(adapter);

    }

    private void getOtherReviewList(){

        Query query = db.collection("users").document(uid).collection("performance")
                .whereEqualTo("show", true)
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ReviewList> response = new FirestoreRecyclerOptions.Builder<ReviewList>()
                .setQuery(query, ReviewList.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ReviewList, ReviewsHolder>(response) {
            @Override
            public void onBindViewHolder(ReviewsHolder holder, int position, ReviewList model) {

                FirebaseStorage fs = FirebaseStorage.getInstance();
                StorageReference sr = fs.getReference().child(uid + "/performance/" + model.getTitle()+model.getDate());
                sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Glide.with(holder.itemView)
                                .load(task.getResult())
                                .into(holder.imageView);
                    }
                });

                holder.textTitle.setText(model.getTitle());
                holder.textDate.setText(model.getDate());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("title", model.getTitle());
                    intent.putExtra("date", model.getDate());
                    intent.putExtra("me", me);
                    startActivity(intent);
                });

                holder.chx.setVisibility(View.GONE);
                holder.textCheck.setVisibility(View.GONE);
            }

            @Override
            public ReviewsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.review_item, group, false);

                return new ReviewsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        reviewList.setAdapter(adapter);

    }


    public class ReviewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textTitle;
        @BindView(R.id.date)
        TextView textDate;
        @BindView(R.id.checkbox)
        CheckBox chx;
        @BindView(R.id.checktext)
        TextView textCheck;

        public ReviewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
//                    다른 유저 리스트 액티비티 추가
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
//                    Toast.makeText(getApplicationContext(), "다른 사용자의 리스트입니다.", Toast.LENGTH_LONG).show();
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
//https://stackoverflow.com/questions/54369948/how-to-implement-a-filter-for-recyclerview-populated-from-firestore
