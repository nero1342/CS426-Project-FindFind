package com.apcs.nero.findfind;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

    public ArrayList<Polyline> drawPath(GoogleMap mMap, ArrayList<Node> people, Node me) {
        ArrayList<Polyline> lines = new ArrayList<>();

        for (Node person : people) {
            PolylineOptions options = new PolylineOptions()
                    .addAll(_path.get(person.getName()))
                    .width(10)
                    .geodesic(true);
            if (person.getName() == me.getName())
                options.color(Color.BLUE);
            Polyline line = mMap.addPolyline((options));
            lines.add(line);
        }
        return lines;
    }
}
