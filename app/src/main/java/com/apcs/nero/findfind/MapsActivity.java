package com.apcs.nero.findfind;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Node> mPeople;
    private ArrayList<Node> mPlaces;
    private int mMinIndex;
    private ArrayList<Polyline> mPath;
    private ArrayList<Marker> mMarkers;
    int numTaskRemaining = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadData();
        initComponents();
    }

    public void loadData() {
        //  Add people
        mPeople = new ArrayList<>();
        mPeople.add(new Node("Khoa", new LatLng(10.751088, 106.699583)));
        mPeople.add(new Node("Ro", new LatLng(10.799199, 106.685738)));

        // Add places
        mPlaces = new ArrayList<>();
        mPlaces.add(new Node("Vietphin Coffee", new LatLng(10.772867, 106.690587)));
        mPlaces.add(new Node("The Coffee House", new LatLng(10.771223, 106.681081)));
        mPlaces.add(new Node("Say Coffee", new LatLng(10.772551, 106.669397)));

        numTaskRemaining = mPlaces.size();
    }

    public void initComponents() {
        mMinIndex = 0;
        for (int i = 0; i < mPlaces.size(); ++i) {
            Node place = mPlaces.get(i);
            try {
                HashMap<String, ArrayList<LatLng>> _path =  (new AsyncTaskLoadDirection(this, place)).execute(mPeople).get();
                place.setPath(_path);
                --numTaskRemaining;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //if (place.getLength() < mPlaces.get(mMinIndex).getLength())
            //    mMinIndex = i;
        }



    }

    private class AsyncTaskLoadDirection extends AsyncTask<ArrayList<Node>, String, HashMap<String, ArrayList<LatLng>>> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        private ProgressDialog _dialog;
        private WeakReference<Node> _place;


        public AsyncTaskLoadDirection(Activity activity, Node place) {
            _dialog = new ProgressDialog(activity);
            _place = new WeakReference<>(place);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _dialog.setTitle("Loading...");
            _dialog.setCancelable(false);
            _dialog.setIndeterminate(true);
            _dialog.show();
        }

//        @Override
//        protected void onProgressUpdate(String... values) {
//            //_dialog.setProgress(Integer.parseInt(values[0]));
//            _dialog.setMessage(values[1] + '/' + values[2] + '(' + values[0] + "%)");
//        }

        @Override
        protected HashMap<String, ArrayList<LatLng>> doInBackground(ArrayList<Node>... people) {
            // Get polypine paths from all people to this place

            try {
                HashMap<String, ArrayList<LatLng>> path = new HashMap<>();
                double length = 0.0;

                String ACCESS_TOKEN = "pk.eyJ1Ijoia2hvYTMxMDEiLCJhIjoiY2tlMHhvMGU1MDd2ajJzbXc3Y3cwNTR0NCJ9.EGFqfMgYQhqgA1WM4MqSDw";
                String SOURCE_API = "https://api.mapbox.com/directions/v5/mapbox/cycling/";
                String PARAMS = "?alternatives=false&geometries=geojson&steps=true&access_token=" + ACCESS_TOKEN;
                for (Node person : people[0]) {
                    final ArrayList<LatLng> coordinates = new ArrayList<>();
                    String coorParams = _place.get().getLocation().longitude + "," +
                            _place.get().getLocation().latitude + ";" +
                            person.getLocation().longitude + "," +
                            person.getLocation().latitude;

                    Uri builtURI = Uri.parse(SOURCE_API + coorParams + "?").buildUpon()
                            .appendQueryParameter("alternatives", "false")
                            .appendQueryParameter("geometries", "geojson")
                            .appendQueryParameter("steps", "true")
                            .appendQueryParameter("access_token", ACCESS_TOKEN)
                            .build();
                    URL requestURL = new URL(builtURI.toString());
                    urlConnection = (HttpURLConnection) requestURL.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();

                    // Read JSON
                    String line;
                    while((line = reader.readLine()) != null) {
                        builder.append(line);
                        builder.append('\n');
                    }
                    if (builder.length() == 0) {
                        return null;
                    }

                    String JSONString = builder.toString();
                    JSONObject response = new JSONObject(JSONString);

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

                        length += Math.hypot(x2-x1, y2-y1);
                        pointLast = point;
                    }
                    path.put(person.getName(), coordinates);
                }
                _place.get().setLength(length);
                return path;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<LatLng>> path) {
            super.onPostExecute(path);
            --numTaskRemaining;
            if (_dialog.isShowing()) {
                _dialog.dismiss();
            }
            //_place.get().setPath(path);
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        while (numTaskRemaining > 0);
        mMarkers = new ArrayList<>();
        mPath = mPlaces.get(mMinIndex).drawPath(mMap, mPeople);
        displayMarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Polyline line : mPath)
                    //  Redraw the path to the new marker
                    line.remove();
                mPath = mPlaces.get(0).drawPath(mMap, mPeople);
                return false;
            }
        });

    }

    public void displayMarkers() {
        //  Display places
        for (Node place : mPlaces) {
            mMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(place.getLocation())
                    .title(place.getName())
            ));
        }

        // Display people
        for (Node person : mPeople) {
            mMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(person.getLocation())
                    .title(person.getName())
            ));
        }

        // Move camera to the optimal place
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mPlaces.get(mMinIndex).getLocation())     // Sets the center of the map to Mountain View
                .zoom(15)                           // Sets the zoom
                .bearing(90)                        // Sets the orientation of the camera to east
                .tilt(30)                           // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}