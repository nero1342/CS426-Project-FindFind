package com.apcs.nero.findfind;

import java.io.Serializable;

public class Infomation implements Serializable {
    String _name;
    LocationInfo _locationInfo;

    public Infomation() {
    }

    public Infomation(String name, LocationInfo locationInfo) {
        this._name = name;
        this._locationInfo = locationInfo;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public LocationInfo getLocationInfo() {
        return _locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this._locationInfo = locationInfo;
    }

    public String getAddress() {
        return _locationInfo.getDesc();
    }
}
