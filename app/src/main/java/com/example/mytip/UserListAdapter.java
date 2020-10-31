package com.example.mytip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {
    private ArrayList<UserItem> itemList = new ArrayList<UserItem>() ;

    // ListViewAdapter의 생성자
    public UserListAdapter() {

    }
    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_item, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.name) ;
        TextView emailView = (TextView) convertView.findViewById(R.id.email) ;

        UserItem item = itemList.get(position);

        nameView.setText(item.getName());
        emailView.setText(item.getEmail());

        return convertView;
    }

    public void addItem(String name, String email, String uid) {
        UserItem item = new UserItem();

        item.setName(name);
        item.setEmail(email);
        item.setUid(uid);

        itemList.add(item);
    }
}
