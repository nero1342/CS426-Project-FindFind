package com.apcs.nero.findfind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class InfomationActivity extends AppCompatActivity {
    EditText _edittextFullname, _edittextLocation;
    Button _btnSave, _btnCancel;
    private AppCompatAutoCompleteTextView autoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        // determineLatLngFromAddress(this, "419 Phan Xích Long, Phường 3");
        initComponents();
        loadData();
    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Infomation infomation = (Infomation) intent.getSerializableExtra("user");
            _edittextFullname.setText(infomation.getFullname());
            _edittextLocation.setText(infomation.getPosition());
        }
    }
//      Test find location by address -> success
//    public LatLng determineLatLngFromAddress(Context appContext, String strAddress) {
//        LatLng latLng = null;
//        Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
//        List<Address> geoResults = null;
//
//        try {
//            geoResults = geocoder.getFromLocationName(strAddress, 10);
//            while (geoResults.size()==0) {
//                geoResults = geocoder.getFromLocationName(strAddress, 10);
//            }
//            if (geoResults.size()>0) {
//                Address addr = geoResults.get(0);
//                latLng = new LatLng(addr.getLatitude(),addr.getLongitude());
//            }
//        } catch (Exception e) {
//            System.out.print(e.getMessage());
//        }
//
//        return latLng; //LatLng value of address
//    }

    private void initComponents() {
        _edittextFullname = (EditText) findViewById(R.id.edittextFullname);
        _edittextLocation = (EditText) findViewById(R.id.edittextLocation);
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
}