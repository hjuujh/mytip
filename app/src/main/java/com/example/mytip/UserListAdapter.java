package com.example.mytip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<UserItem> itemList = new ArrayList<UserItem>() ;
    private ArrayList<UserItem> filteredItemList = itemList ;
    private Filter listFilter;

    public UserListAdapter() {

    }
    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredItemList.get(i);
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

        UserItem item = filteredItemList.get(position);

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

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }

        return listFilter ;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = itemList ;
                results.count = itemList.size() ;
            } else {
                ArrayList<UserItem> itemFiterList = new ArrayList<>() ;

                for (UserItem item : itemList) {
                    if (item.getName().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemFiterList.add(item) ;
                    }
                }

                results.values = itemFiterList ;
                results.count = itemFiterList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredItemList = (ArrayList<UserItem>) results.values ;

            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
