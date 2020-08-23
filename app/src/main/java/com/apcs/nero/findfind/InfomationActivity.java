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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class InfomationActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText _edittextName;
    AppCompatAutoCompleteTextView _edittextLocation;
    Infomation _user = null;
    LocationInfo _locationInfo = null;
    private GoogleMap mMap;
    Marker mMarker = null;
    private LocationManager mLocationManager = null;
    private Button _btnFindLocation, _btnSave, _btnCancel;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_Infomation);
        mapFragment.getMapAsync(this);
        initComponents();
        loadData();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location currentLocation = getCurrentLocation();
        _locationInfo = determineLocationFromLatLong(getApplicationContext(), new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude()));
        EditText editText = (EditText) findViewById(R.id.edittextDescLoction);
        editText.setText(_locationInfo.getDesc());
        displayMarker(_locationInfo);
    }

    private void loadData() {
        Intent intent = getIntent();

        try {
            _user = (Infomation) intent.getSerializableExtra("user");
            _edittextName.setText(_user.getName());
            _edittextLocation.setText(_user.getAddress());
        } catch (Exception e) {
        }
        try {
            getSupportActionBar().setTitle(intent.getStringExtra("title"));
        } catch (Exception e) {

        }
        if (_user == null) _user = new Infomation();
    }

    private void initComponents() {
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();

        if (checkPermission()) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        _btnFindLocation = (Button)findViewById(R.id.btnFindLocation);

        _edittextName = (EditText) findViewById(R.id.edittextName);
        _edittextLocation = (AppCompatAutoCompleteTextView) findViewById(R.id.edittextLocation);
        _edittextLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String st = s.toString();
                if (st.isEmpty()) {
                    _btnFindLocation.setText("Find current location");
                }
                else {
                    _btnFindLocation.setText("Find location");
                }
            }
        });
        //
        _btnSave = (Button) findViewById(R.id.btnSaveAndClose);
        _btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _user = new Infomation(_edittextName.getText().toString(), _locationInfo);
                if (!_edittextLocation.getText().toString().equals(_locationInfo.getDesc())) {
                    String a = _edittextLocation.getText().toString();
                    String b = _locationInfo.getDesc();
                    boolean c = (a == b);
                    Toast.makeText(getApplicationContext(), "Location and Real location must be the same", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("user", _user);
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
        if (address.isEmpty()) {
            Location currentLocation = getCurrentLocation();
            _locationInfo = determineLocationFromLatLong(getApplicationContext(), new LatLong(currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else _locationInfo = determineLocationFromAddress(getApplicationContext(), address);
        if (_locationInfo != null) {
            EditText editText = (EditText) findViewById(R.id.edittextDescLoction);
            editText.setText(_locationInfo.getDesc());
            displayMarker(_locationInfo);
        }
        else {
            Toast.makeText(this, "Fail to find location",Toast.LENGTH_LONG).show();
        }
    }

    private void displayMarker(LocationInfo locationInfo) {
        if (mMarker != null) mMarker.remove();
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(locationInfo.getLocation().toLatLng())
                .title(locationInfo.getDesc())
        );
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locationInfo.getLocation().toLatLng())
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);

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
        _user.setLocationInfo(_locationInfo);
        EditText editText = (EditText) findViewById(R.id.edittextDescLoction);
        _edittextLocation.setText(editText.getText());
    }

    public LocationInfo determineLocationFromAddress(Context appContext, String strAddress) {
        String desc = null;
        LatLong location = null;
        LocationInfo locationInfo = null;

        Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
        List<Address> geoResults = null;

        try {
            geoResults = geocoder.getFromLocationName(strAddress, 1);
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                location = new LatLong(addr.getLatitude(), addr.getLongitude());
                desc = addr.getAddressLine(0);
                locationInfo = new LocationInfo(location, desc);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return locationInfo; //LatLong value of address
    }

    public LocationInfo determineLocationFromLatLong(Context appContext, LatLong location) {
        String desc = null;
        LocationInfo locationInfo = null;

        Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
        List<Address> geoResults = null;

        try {
            geoResults = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                location = new LatLong(addr.getLatitude(), addr.getLongitude());
                desc = addr.getAddressLine(0);
                locationInfo = new LocationInfo(location, desc);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return locationInfo; //LatLong value of address
    }
}