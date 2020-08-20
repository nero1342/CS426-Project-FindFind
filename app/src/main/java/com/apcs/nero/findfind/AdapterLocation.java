package com.apcs.nero.findfind;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterLocation extends ArrayAdapter<LocationInfo> {
    private Context _context;
    private int _layoutID;
    private ArrayList<LocationInfo> _items;

    public AdapterLocation(@NonNull Context context, int resource, @NonNull List<LocationInfo> objects) {
        super(context, resource, objects);
        _context = context;
        _layoutID = resource;
        _items = (ArrayList) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(_context);
            convertView = inflater.inflate(_layoutID, null, false);
        }
        LocationInfo locationInfo = _items.get(position);
        //TextView textView_fullname = (TextView) convertView.findViewById(R.id.textView_fullname);
        //TextView textView_location = (TextView) convertView.findViewById(R.id.textView_location);

        //textView_fullname.setText(infomation.getFullname());
        //textView_location.setText(infomation.getPosition());
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return locationFilter;
    }

    private Filter locationFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };
}