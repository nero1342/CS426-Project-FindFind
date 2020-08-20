package com.apcs.nero.findfind;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationInfo {
    LatLng _location;
    String _desc;

    public LocationInfo(LatLng location, String desc) {
        this._desc = desc;
        this._location = location;
    }

    public LatLng getLocation() {
        return _location;
    }

    public String getDesc() {
        return _desc;
    }

    public void setLocation(LatLng location) {
        this._location = location;
    }

    public void setDesc(String desc) {
        this._desc = desc;
    }
}
