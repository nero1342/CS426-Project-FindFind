package com.apcs.nero.findfind;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Node> mPeople;
    private ArrayList<Node> mPlaces;
    private int mMinIndex;
    private ArrayList<Polyline> mPath;
    private ArrayList<Marker> mMarkers;

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
    }

    public void initComponents() {
        mMinIndex = 0;
        for (int i = 0; i < mPlaces.size(); ++i) {
            Node place = mPlaces.get(i);
            place.preparePath(mPeople);
            if (place.getLength() < mPlaces.get(mMinIndex).getLength())
                mMinIndex = i;
        }

        mMarkers = new ArrayList<>();
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