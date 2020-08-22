package com.apcs.nero.findfind;

import android.location.Location;

import java.io.Serializable;

public class LocationInfo implements Serializable {
    LatLong _location;
    String _desc;

    public LocationInfo(LatLong location, String desc) {
        this._desc = desc;
        this._location = location;
    }

    public LatLong getLocation() {
        return _location;
    }

    public String getDesc() {
        return _desc;
    }

    public void setLocation(LatLong location) {
        this._location = location;
    }

    public void setDesc(String desc) {
        this._desc = desc;
    }
}
