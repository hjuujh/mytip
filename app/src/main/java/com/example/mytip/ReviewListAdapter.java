package com.example.mytip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> implements Filterable {
    private ArrayList<ReviewItem> itemList = new ArrayList<>();
    private ArrayList<ReviewItem> filteredItemList = new ArrayList<>();
    private String uid;
    private boolean me;
    private Context context;
    private boolean checked;
    private FirebaseFirestore db;
    private int selected;

    public ReviewListAdapter(ArrayList<ReviewItem> list, boolean me, String uid, int selected) {
        super();
        this.itemList = list;
        this.filteredItemList = list;
        this.me = me;
        this.uid = uid;
        this.selected = selected;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.review_item, parent, false) ;
        ReviewListAdapter.ViewHolder vh = new ReviewListAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(ReviewListAdapter.ViewHolder holder, int position) {

        ReviewItem item = filteredItemList.get(position) ;

        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReference().child("performance/" + item.getKey());
        sr.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(holder.itemView)
                        .load(task.getResult())
                        .into(holder.imageView);
            }
        });

        holder.textTitle.setText(item.getTitle());
        holder.textDate.setText(item.getDate());
        if(me){
            holder.chx.setChecked(Boolean.parseBoolean(item.getShow()));
            holder.chx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Boolean.parseBoolean(item.getShow())){
                        checked = false;
                        item.setShow("false");
                    }
                    else {
                        checked = true;
                        item.setShow("true");
                    }
                    db.collection("users").document(uid).collection("performance")
                            .document(item.getKey())
                            .update("show", checked)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("성공");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            });
        }
        else {
            holder.chx.setVisibility(View.GONE);
            holder.textCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("date", item.getDate());
            intent.putExtra("key", item.getKey());
            intent.putExtra("me", me);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textTitle;
        TextView textDate;
        CheckBox chx;
        TextView textCheck;

        ViewHolder(View itemView) {
            super(itemView) ;

            imageView = (ImageView) itemView.findViewById(R.id.image);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            textDate = (TextView) itemView.findViewById(R.id.date);
            textCheck = (TextView) itemView.findViewById(R.id.checktext);
            chx = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredItemList = itemList;
                } else {
                    ArrayList<ReviewItem> itemFiterList = new ArrayList<>() ;
                    for(ReviewItem item : itemList) {
                        switch (selected){
                            case 1:
                                if(item.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                                    itemFiterList.add(item);
                                }
                                break;
                            case 2:
                                if(item.getReview().toLowerCase().contains(charString.toLowerCase())) {
                                    itemFiterList.add(item);
                                }
                                break;
                        }

                    }
                    filteredItemList = itemFiterList;
                }
                FilterResults results = new FilterResults() ;
                results.values = filteredItemList;
                results.count = filteredItemList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemList = (ArrayList<ReviewItem>)results.values;
                notifyDataSetChanged();
            }
        };
    }
}
