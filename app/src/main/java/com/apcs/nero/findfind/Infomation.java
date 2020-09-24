package com.apcs.nero.findfind;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Infomation implements Serializable {
    String _name;
    LocationInfo _locationInfo;
    int _ID;

    public Infomation(String name, LocationInfo locationInfo, int ID) {
        this._name = name;
        this._locationInfo = locationInfo;
        this._ID = ID;
    }

    public Infomation() {
    }

    public Infomation(String name, LocationInfo locationInfo) {
        this._name = name;
        this._locationInfo = locationInfo;
    }

    public Infomation(JSONObject jsonObject) {
        try {
            this._name = jsonObject.getString("name");
            this._ID = jsonObject.getInt("id");
            this._locationInfo = new LocationInfo(jsonObject.getJSONObject("locationInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public int getID() {
        return _ID;
    }

    public void setID(int ID) {
        this._ID = ID;
    }
}
