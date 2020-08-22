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
    }

    public String getName() { return _name; }

    public void setName(String name) { _name = name; }

    public LatLng getLocation() { return _location; }

    public void setLocation(LatLng location) { _location = location; }

    public double getLength() { return _length; }

    public void preparePath(ArrayList<Node> people) {
        final Context context = MySuperAppApplication.getContext();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        String ACCESS_TOKEN = "pk.eyJ1Ijoia2hvYTMxMDEiLCJhIjoiY2tlMHhvMGU1MDd2ajJzbXc3Y3cwNTR0NCJ9.EGFqfMgYQhqgA1WM4MqSDw";
        String SOURCE_API = "https://api.mapbox.com/directions/v5/mapbox/cycling/";
        String PARAMS = "?alternatives=false&geometries=geojson&steps=true&access_token=" + ACCESS_TOKEN;

        for (Node person : people) {
            final ArrayList<LatLng> coordinates = new ArrayList<>();

            String coorParams = _location.longitude + "%2C" +
                                _location.latitude + "%3B" +
                                person.getLocation().longitude + "%2C" +
                                person.getLocation().latitude;

            String url = SOURCE_API + coorParams + PARAMS;

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject geometry = response.getJSONArray("routes").getJSONObject(0).getJSONObject("geometry");
                                JSONArray jsonCoordinates = geometry.getJSONArray("coordinates");

                                for (int i = 0; i < jsonCoordinates.length(); i++) {
                                    JSONArray singleCoordinate = jsonCoordinates.getJSONArray(i);
                                    coordinates.add(new LatLng(
                                            singleCoordinate.getDouble(1),
                                            singleCoordinate.getDouble(0)
                                    ));
                                }

                                // Compute the total length of the path
                                LatLng pointLast = coordinates.get(0);
                                for (int i = 1; i < coordinates.size(); i++) {
                                    LatLng point = coordinates.get(i);
                                    double x1 = point.latitude;
                                    double y1 = point.longitude;
                                    double x2 = pointLast.latitude;
                                    double y2 = pointLast.longitude;

                                    _length += Math.hypot(x2-x1, y2-y1);
                                    pointLast = point;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Toast.makeText(context, "Error in connection to server", Toast.LENGTH_SHORT).show();
                        }
                    });

            _path.put(person.getName(), coordinates);

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
    }

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
