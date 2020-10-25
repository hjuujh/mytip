package com.example.mytip;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

//    public void addItem(Uri ticket, String title, String date) {
//        ListViewItem item = new ListViewItem();
//
//        item.setUri(ticket);
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

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
//        ImageView ImageView = (ImageView) convertView.findViewById(R.id.ticket) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
//        ImageView.setImageBitmap(listViewItem.getTicket());
//        ImageView.setImageURI(listViewItem.getUri());
        titleTextView.setText(listViewItem.getTitle());
        dateTextView.setText(listViewItem.getDate());
//        Glide.with(context)
//                .load(listViewItem.getUri())
//                .into(ImageView);

        return convertView;
    }
}
