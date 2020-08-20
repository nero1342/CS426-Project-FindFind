package com.apcs.nero.findfind;

import java.io.Serializable;

public class Infomation implements Serializable {
    String _fullname;
    String _location;

    public Infomation(String fullname, String location) {
        this._fullname = fullname;
        this._location = location;
    }

    public String getFullname() {
        return _fullname;
    }

    public String getPosition() {
        return _location;
    }

    public void setFullname(String fullname) {
        this._fullname = fullname;
    }

    public void setPosition(String location) {
        this._location = location;
    }
}
