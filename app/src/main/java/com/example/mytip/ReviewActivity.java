package com.example.mytip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {
//    @BindView(R.id.getdata)
    private Button btn;
    private String uid, title, date;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    @BindView(R.id.addbtn)
    Button addBtn;
    @BindView(R.id.review_list)
    RecyclerView reviewList;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        ButterKnife.bind(this);
        init();
        getReviewList();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TicketActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        reviewList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getReviewList(){
        Context mContext;

        Query query = db.collection("users").document(uid).collection("performance");

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
                    Snackbar.make(reviewList, model.getTitle()+" at "+model.getDate(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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

    public class ReviewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textTitle;
        @BindView(R.id.date)
        TextView textDate;

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

}
