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

public class ReviewListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<ReviewItem> itemList = new ArrayList<ReviewItem>() ;
    private ArrayList<ReviewItem> filteredItemList = itemList ;
    private Filter listFilter;
    private int selected;

    public ReviewListAdapter(int selected) {
        this.selected = selected;
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
            convertView = inflater.inflate(R.layout.search_item, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.name) ;
        TextView dateView = (TextView) convertView.findViewById(R.id.date) ;
        TextView titleView = (TextView) convertView.findViewById(R.id.title) ;

        ReviewItem item = filteredItemList.get(position);

        nameView.setText(item.getUname());
        dateView.setText(item.getDate());
        titleView.setText(item.getTitle());

        return convertView;
    }

    public void addItem(String name, String date, String title, String uid, String review) {
        ReviewItem item = new  ReviewItem();

        item.setUname(name);
        item.setDate(date);
        item.setTitle(title);
        item.setUid(uid);
        item.setReview(review);

        itemList.add(item);
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ReviewListAdapter.ListFilter() ;
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
                ArrayList<ReviewItem> itemFiterList = new ArrayList<>() ;

                for (ReviewItem item : itemList) {

                    switch (selected){
                        case 1:
                            if (item.getTitle().toUpperCase().contains(constraint.toString().toUpperCase()))
                            {
                                itemFiterList.add(item) ;
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            if (item.getReview().toUpperCase().contains(constraint.toString().toUpperCase()))
                            {
                                itemFiterList.add(item) ;
                            }
                    }

                }

                results.values = itemFiterList ;
                results.count = itemFiterList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredItemList = (ArrayList<ReviewItem>) results.values ;

            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
