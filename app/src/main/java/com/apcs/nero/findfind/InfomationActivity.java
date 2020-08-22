package com.apcs.nero.findfind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import android.location.LocationListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class InfomationActivity extends FragmentActivity implements OnMapReadyCallback {
    EditText _edittextFullname;
    Button _btnSave, _btnCancel;
    AppCompatAutoCompleteTextView _edittextLocation;

    private GoogleMap mMap;
    Marker mMarker = null;
    private LocationManager mLocationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initComponents();
        loadData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location currentLocation = getCurrentLocation();
        LocationInfo locationInfo = determineLocationFromLatLng(getApplicationContext(), new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        EditText editText = (EditText) findViewById(R.id.edittextDescLoction);
        editText.setText(locationInfo.getDesc());
        displayMarker(locationInfo);
    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Infomation infomation = (Infomation) intent.getSerializableExtra("user");
            _edittextFullname.setText(infomation.getFullname());
            _edittextLocation.setText(infomation.getPosition());
        }
    }
    public LocationInfo determineLocationFromAddress(Context appContext, String strAddress) {
        String desc = null;
        LatLng location = null;
        LocationInfo locationInfo = null;

        Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
        List<Address> geoResults = null;

        try {
            geoResults = geocoder.getFromLocationName(strAddress, 1);
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                location = new LatLng(addr.getLatitude(), addr.getLongitude());
                desc = addr.getAddressLine(0);
                locationInfo = new LocationInfo(location, desc);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return locationInfo; //LatLng value of address
    }

    public LocationInfo determineLocationFromLatLng(Context appContext, LatLng location) {
        String desc = null;
        LocationInfo locationInfo = null;

        Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
        List<Address> geoResults = null;

        try {
            geoResults = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                location = new LatLng(addr.getLatitude(), addr.getLongitude());
                desc = addr.getAddressLine(0);
                locationInfo = new LocationInfo(location, desc);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return locationInfo; //LatLng value of address
    }

    private void initComponents() {

        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();

        if (checkPermission()) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

        _edittextFullname = (EditText) findViewById(R.id.edittextFullname);
        _edittextLocation = (AppCompatAutoCompleteTextView) findViewById(R.id.edittextLocation);
        //
        _btnSave = (Button) findViewById(R.id.btnSaveAndClose);
        _btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Infomation infomation = new Infomation(_edittextFullname.getText().toString(), _edittextLocation.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("user", infomation);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        //
        _btnCancel = (Button) findViewById(R.id.btnCancel);
        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    public void findLocationOnClick(View view) {
        String address = _edittextLocation.getText().toString();
        LocationInfo locationInfo = determineLocationFromAddress(getApplicationContext(), address);
        if (locationInfo != null) {
            EditText editText = (EditText) findViewById(R.id.edittextDescLoction);
            editText.setText(locationInfo.getDesc());
            displayMarker(locationInfo);
        }
        else {
            Toast.makeText(this, "Fail to find location",Toast.LENGTH_LONG);
        }
    }

    private void displayMarker(LocationInfo locationInfo) {
        if (mMarker != null) mMarker.remove();
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(locationInfo.getLocation())
                .title(locationInfo.getDesc())
        );
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locationInfo.getLocation())
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return false;
        }
        return true ;
    }
    private Location getCurrentLocation() {
        checkPermission();
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    public void chooseLocationOnClick(View view) {
        EditText editText = (EditText) findViewById(R.id.edittextDescLoction);
        _edittextLocation.setText(editText.getText());
    }
}