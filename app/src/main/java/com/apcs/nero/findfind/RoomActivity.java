package com.apcs.nero.findfind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_FIND_BEST_PLACE = 0x9333;
    private static final int REQUEST_CODE_ADD_ROOMMATE = 0x9345;
    private static final int REQUEST_CODE_ADD_PLACE = 0x9332;
    private ArrayList<Infomation> _roommates = new ArrayList<>();
    private ArrayList<Infomation> _places = new ArrayList<>();
    private AdapterInfomation _adapterRoommates, _adapterPlaces;
    private ListView _listviewRoommates = null;
    private ListView _listviewPlaces = null;
    private int _roomID;
    private BackendRoom _backendRoom;
    private Infomation _user;
    private TextView _textViewStatus;
    private int isProcess = 0;

    Handler _handler = new Handler();
    private Runnable _runnableUpdateData;
    private String _versionMem, _versionLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        loadData();
        initComponents();
    }

    private void loadData() {
        _textViewStatus = new TextView(this);

        Intent intent = getIntent();
        Infomation current_user = (Infomation) intent.getSerializableExtra("user");
        String BACKEND_IP_ADDRESS = (String)intent.getSerializableExtra("backendRoomIP");
        _backendRoom = new BackendRoom(BACKEND_IP_ADDRESS, getApplicationContext(), _textViewStatus, RoomActivity.this, isProcess);

        _roomID = (int) intent.getIntExtra("roomID", -1);
        _user = current_user;
        //
        _roommates.add(current_user);
        isProcess = _backendRoom.ADD_ROOMMATE;
        _backendRoom.addItem(_user, _roomID, "mem");
        //
    }

    private void updateListviewRoommates() { _adapterRoommates.notifyDataSetChanged(); }
    private void updateListviewPlaces() {
        _adapterPlaces.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener remove_when_click_roommates = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                Toast.makeText(getApplicationContext(), "Can not remove anyone", Toast.LENGTH_LONG).show();
                return;
            }
            _roommates.remove(position);
            updateListviewRoommates();
        }
    };
    AdapterView.OnItemClickListener remove_when_click_places = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _places.remove(position);
            Toast.makeText(getApplicationContext(), "Remove place at position " + ((Integer)position).toString() + " successfully", Toast.LENGTH_LONG).show();
            updateListviewPlaces();
        }
    };
    private void initComponents() {
        _listviewRoommates = findViewById(R.id.listRoommate);
        _adapterRoommates = new AdapterInfomation(this, R.layout.listview_items, _roommates);
        _listviewRoommates.setAdapter(_adapterRoommates);

        _listviewPlaces = findViewById(R.id.listLocation);
        _adapterPlaces = new AdapterInfomation(this, R.layout.listview_items, _places);
        _listviewPlaces.setAdapter(_adapterPlaces);

        _listviewRoommates.setOnItemClickListener(remove_when_click_roommates);
        _listviewPlaces.setOnItemClickListener(remove_when_click_places);

        ImageButton btnAddRoommate = (ImageButton) findViewById(R.id.btnAddRoommate);
        btnAddRoommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This feature is temporaryly locked in this version.", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(RoomActivity.this, InfomationActivity.class);
//                intent.putExtra("title", "Add New Roommate");
//                startActivityForResult(intent, REQUEST_CODE_ADD_ROOMMATE);
            }
        });

        ImageButton btnAddPlace = (ImageButton) findViewById(R.id.btnAddLocation);
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, InfomationActivity.class);
                intent.putExtra("title", "Add New Place");
                startActivityForResult(intent, REQUEST_CODE_ADD_PLACE);
            }
        });

        Button btnFind = (Button) findViewById(R.id.btnFindBestLocation);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, MapsActivity.class);
                if (_roommates.size() == 0) {
                    Toast.makeText(getApplicationContext(), "There is no user in this room", Toast.LENGTH_LONG).show();
                    return;
                }
                if (_places.size() == 0) {
                    Toast.makeText(getApplicationContext(), "There is no candidate place", Toast.LENGTH_LONG).show();
                    return;
                }
                intent.putExtra("people",_roommates);
                intent.putExtra("location", _places);
                startActivityForResult(intent, REQUEST_CODE_FIND_BEST_PLACE);
            }
        });

        TextView textViewRoomID = (TextView) findViewById(R.id.textViewRoomID);
        textViewRoomID.setText(((Integer)_roomID).toString());


        // Used to be live status when asynchronous volley completed. Data will be stored inside _textViewStatus
        _textViewStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString();
                switch (isProcess) {
                    case BackendRoom.ADD_PLACE: {
                        isProcess = 0;
                        break;
                    }
                    case BackendRoom.ADD_ROOMMATE: {
                        _user.setID(Integer.parseInt(data));
                        isProcess = 0;
                        break;
                    }
                    case BackendRoom.REMOVE_PLACE: {
                        isProcess = 0;
                        break;
                    }
                    case BackendRoom.REMOVE_ROOMMATE: {
                        isProcess = 0;
                        break;
                    }
                    case BackendRoom.UPDATE_ROOMMATE: {
                        try {
                            JSONArray jsonArray = new JSONArray(data);
                            _roommates.clear();
                            for (int i = 0; i < (int)jsonArray.length(); ++i) {
                                Infomation info = new Infomation(jsonArray.getJSONObject(i));
                                _roommates.add(info);
                            }
                            updateListviewRoommates();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        _handler.postDelayed(_runnableUpdateData, 0); // first run instantly
                        isProcess = 0;
                        break;
                    }
                    case BackendRoom.UPDATE_PLACE: {
                        try {
                            JSONArray jsonArray = new JSONArray(data);
                            _places.clear();
                            for (int i = 0; i < (int)jsonArray.length(); ++i) {
                                Infomation info = new Infomation(jsonArray.getJSONObject(i));
                                _places.add(info);
                            }
                            updateListviewPlaces();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        _handler.postDelayed(_runnableUpdateData, 0); // first run instantly
                        isProcess = 0;
                        break;
                    }
                    case BackendRoom.CHECK_UPDATE_ROOMMATE: {
                        if (!data.equals(_versionMem)) {
                            // Stop check
                            _handler.removeCallbacks(_runnableUpdateData);
                            //

                            isProcess = BackendRoom.UPDATE_ROOMMATE;
                            _versionMem = data;
                            _backendRoom.get(_roomID, "mem");
                        }

                        break;
                    }
                    case BackendRoom.CHECK_UPDATE_PLACE: {
                        if (!data.equals(_versionLoc)) {
                            // Stop check
                            _handler.removeCallbacks(_runnableUpdateData);
                            //
                            isProcess = BackendRoom.UPDATE_PLACE;
                            _versionLoc = data;
                            _backendRoom.get(_roomID, "loc");
                        }
                        break;
                    }
                }
            }
        });

        _runnableUpdateData = new Runnable() {
            @Override
            public void run() {
                _handler.postDelayed(this, 2 * 1000); // every 2 seconds
                checkUpdateDataFromServer();
            }
        };
        _handler.postDelayed(_runnableUpdateData, 5 * 1000); // first run instantly
    }
    int countCheckUpdate = 0;
    private void checkUpdateDataFromServer() {
        countCheckUpdate ^= 1;
        String type = "";
        if (countCheckUpdate == 0) {
            isProcess = BackendRoom.CHECK_UPDATE_ROOMMATE;
            type = "mem";
        } else {
            isProcess = BackendRoom.CHECK_UPDATE_PLACE;
            type = "loc";
        }
        _backendRoom.version(_roomID, type);

    }

    @Override
    public void onBackPressed() {
        isProcess = BackendRoom.REMOVE_ROOMMATE;
        _backendRoom.removeItem(_user.getID(), "mem");
        _handler.removeCallbacks(_runnableUpdateData);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_ROOMMATE: {
                if (resultCode == Activity.RESULT_OK) {
                    Infomation newRoommate = (Infomation) data.getSerializableExtra("user");
                    _roommates.add(newRoommate);
                    updateListviewRoommates();
                }
                else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_CODE_ADD_PLACE: {
                if (resultCode == Activity.RESULT_OK) {
                    Infomation newLocation = (Infomation) data.getSerializableExtra("user");
                    _places.add(newLocation);
                    updateListviewPlaces();
                    //
                    isProcess = BackendRoom.ADD_PLACE;
                    _backendRoom.addItem(newLocation, _roomID, "loc");
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default: break;
        }
    }
}