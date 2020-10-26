package com.example.mytip;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    private Activity act;

    public ListViewAdapter(Activity act) {
        this.act = act;
      }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    public void addItem(Uri ticket, String title, String date) {
        ListViewItem item = new ListViewItem();

        item.setUri(ticket);
        item.setTitle(title);
        item.setDate(date);

        listViewItemList.add(item);
    }
//    public void addItem(Bitmap ticket, String title, String date) {
//        ListViewItem item = new ListViewItem();
//
//        item.setTicket(ticket);
//        item.setTitle(title);
//        item.setDate(date);
//
//        listViewItemList.add(item);
//    }
    public void addItem(String title, String date) {
        ListViewItem item = new ListViewItem();

        item.setTitle(title);
        item.setDate(date);

        listViewItemList.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        ImageView ImageView = (ImageView) convertView.findViewById(R.id.ticket) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date) ;

        ListViewItem listViewItem = listViewItemList.get(position);

//        Bitmap bm = MediaStore.Images.media.getbitmap(listViewItem.getUri());
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), listViewItem.getUri());
            ImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        ImageView.setImageURI(listViewItem.getUri());
        titleTextView.setText(listViewItem.getTitle());
        dateTextView.setText(listViewItem.getDate());
        System.out.println("##############2");
        System.out.println(listViewItem.getUri());
//        Glide.with(context)
//                .load(listViewItem.getUri())
//                .into(ImageView);

        return convertView;
    }
}
