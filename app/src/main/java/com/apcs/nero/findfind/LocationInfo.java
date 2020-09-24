package com.apcs.nero.findfind;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LocationInfo implements Serializable {
    LatLong _location;
    String _desc;

    public LocationInfo(LatLong location, String desc) {
        this._desc = desc;
        this._location = location;
    }

    public LocationInfo(JSONObject jsonObject) {
        try {
            this._desc = jsonObject.getString("desc");
            this._location = new LatLong(jsonObject.getJSONObject("location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
