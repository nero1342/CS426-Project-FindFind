package com.apcs.nero.findfind;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    String _name;
    LatLng _location;
    HashMap<String, ArrayList<LatLng>> _path;
    double _length;

    public Node(String name, LatLng location) {
        _name = name;
        _location = location;
        _length = 0.0;
        _path = new HashMap<>();
    }

    public String getName() { return _name; }

    public void setName(String name) { _name = name; }

    public LatLng getLocation() { return _location; }

    public void setLocation(LatLng location) { _location = location; }

    public double getLength() { return _length; }

    public void setLength(double length) { _length = length; }

    public void setPath(HashMap<String, ArrayList<LatLng>> path) { _path = path; }

    public ArrayList<Polyline> drawPath(GoogleMap mMap, ArrayList<Node> people) {
        ArrayList<Polyline> lines = new ArrayList<>();

        for (Node person : people) {
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(_path.get(person.getName()))
                    .width(10)
                    .geodesic(true));
            lines.add(line);
        }
        return lines;
    }
}
