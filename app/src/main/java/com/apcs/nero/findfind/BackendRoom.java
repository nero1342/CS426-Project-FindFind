package com.apcs.nero.findfind;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.lang.ref.WeakReference;

public class BackendRoom implements Serializable {
    String _IP_ADDRESS = "http://10.0.0.102:8000/";
    Context _context;
    TextView _textViewResponse;
    Integer _processStatus;

    Activity _activity;
    private ProgressDialog mDialog;


    public static final int CREATE_ROOM = 0x12341;
    public static final int CHECK_ROOM = 0x12342;
    public static final int JOIN_ROOM = 0x12343;
    public static final int ADD_PLACE = 0x12345;
    public static final int ADD_ROOMMATE = 0x12346;
    public static final int REMOVE_PLACE = 0x12347;
    public static final int REMOVE_ROOMMATE = 0x12348;
    public static final int UPDATE_ROOMMATE = 0x12349;
    public static final int UPDATE_PLACE = 0x12340;
    public static final int CHECK_UPDATE_ROOMMATE = 0x1233;
    public static final int CHECK_UPDATE_PLACE = 0x1234;


    public BackendRoom() {
    }

    public BackendRoom(String IP_ADDRESS) {
        this._IP_ADDRESS = IP_ADDRESS;
    }

    public BackendRoom(Context context, TextView textViewResponse, Activity activity, Integer processStatus) {
        this._context = context;
        this._textViewResponse = textViewResponse;
        this._activity = activity;
        this._processStatus = processStatus;
    }
    public BackendRoom(String IP_ADDRESS, Context context, TextView textViewResponse, Activity activity, Integer processStatus) {
        this._IP_ADDRESS = IP_ADDRESS;
        this._context = context;
        this._textViewResponse = textViewResponse;
        this._activity = activity;
        this._processStatus = processStatus;
    }

    public String getIP_ADDRESS() {
        return _IP_ADDRESS;
    }

    public void setIP_ADDRESS(String IP_ADDRESS) {
        this._IP_ADDRESS = IP_ADDRESS;
    }

    private void request(String url) {
        RequestQueue queue = Volley.newRequestQueue(_context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _textViewResponse.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT);
                _textViewResponse.setText("-1");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void requestWithDialog(String url) {
        mDialog = new ProgressDialog(_activity);
        mDialog.setMessage("loading...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();
        RequestQueue queue = Volley.newRequestQueue(_context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _textViewResponse.setText(response);
                        mDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT);
                _textViewResponse.setText("-1");
                mDialog.dismiss();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void createRoomIDFromServer() {
        String url = _IP_ADDRESS + "create";
        _processStatus = CREATE_ROOM;
        requestWithDialog(url);
    }

    public void checkValidRoom(int roomID) {
        String url = _IP_ADDRESS + "check?ID=" + ((Integer)roomID).toString();
        _processStatus = CHECK_ROOM;
        requestWithDialog(url);
    }
    public void joinRoom(Infomation user, int roomID) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(user);
            String url = _IP_ADDRESS + "join?Info=" + json + "&RoomID=" + ((Integer)roomID).toString();
            _processStatus = JOIN_ROOM;
            requestWithDialog(url);
        } catch (Exception e) {
            Toast.makeText(_context, "Failed when joining room", Toast.LENGTH_SHORT).show();
        }
    }

    public void addItem(Infomation info, int roomID, String type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(info);
            String url = _IP_ADDRESS + "add?Info=" + json + "&RoomID=" + ((Integer)roomID).toString() + "&Type=" + type;
            _processStatus = ADD_ROOMMATE;
            requestWithDialog(url);
        } catch (Exception e) {
            Toast.makeText(_context, "Failed when joining room", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeItem(int ID, String type) {
        String url = _IP_ADDRESS + "remove?ID=" + ((Integer)ID).toString() + "&Type=" + type;
        _processStatus = REMOVE_ROOMMATE;
        requestWithDialog(url);
    }

    public void get(int roomID, String type) {
        String url = _IP_ADDRESS + "get?RoomID=" + ((Integer)roomID).toString() + "&Type=" + type;
        _processStatus = UPDATE_ROOMMATE;
        requestWithDialog(url);
    }

    public void version(int roomID, String type) {
        String url = _IP_ADDRESS + "ver?RoomID=" + ((Integer)roomID).toString() + "&Type=" + type;
        _processStatus = CHECK_UPDATE_ROOMMATE;
        request(url);
    }

}
