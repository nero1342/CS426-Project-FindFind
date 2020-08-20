package com.apcs.nero.findfind;

import android.content.Context;
import android.icu.text.IDNA;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterInfomation extends ArrayAdapter<Infomation> {
    private Context _context;
    private int _layoutID;
    private ArrayList<Infomation> _items;
    public AdapterInfomation(@NonNull Context context, int resource, @NonNull List<Infomation> objects) {
        super(context, resource, objects);
        _context = context;
        _layoutID = resource;
        _items = (ArrayList<Infomation>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(_context);
            convertView = inflater.inflate(_layoutID, null, false);
        }
        Infomation infomation = _items.get(position);
        TextView textView_fullname = (TextView) convertView.findViewById(R.id.textView_fullname);
        TextView textView_location = (TextView) convertView.findViewById(R.id.textView_location);

        textView_fullname.setText(infomation.getFullname());
        textView_location.setText(infomation.getPosition());
        return convertView;
    }
}
