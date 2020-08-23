package com.apcs.nero.findfind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_FIND_BEST_LOCATION = 0x9333;
    private static final int REQUEST_CODE_ADD_ROOMMATE = 0x9345;
    private static final int REQUEST_CODE_ADD_LOCATION = 0x9332;
    private ArrayList<Infomation> _roommates = new ArrayList<>();
    private ArrayList<Infomation> _locations = new ArrayList<>();
    private AdapterInfomation _adapterRoommates, _adapterLocations;
    private ListView _listviewRoommates = null;
    private ListView _listviewLocations = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        loadData();
        initComponents();
    }

    private void loadData() {
        Intent intent = getIntent();
        Infomation current_user = (Infomation) intent.getSerializableExtra("user");
        _roommates.add(current_user);

    }
    private void updateListviewRoommates() { _adapterRoommates.notifyDataSetChanged(); }
    private void updateListviewLocations() {
        _adapterLocations.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener remove_when_click_roommates = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                Toast.makeText(getApplicationContext(), "Can not remove myself", Toast.LENGTH_LONG).show();
                return;
            }
            _roommates.remove(position);
            updateListviewRoommates();
        }
    };
    AdapterView.OnItemClickListener remove_when_click_locations = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _locations.remove(position);
            updateListviewLocations();
        }
    };
    private void initComponents() {
        _listviewRoommates = findViewById(R.id.listRoommate);
        _adapterRoommates = new AdapterInfomation(this, R.layout.listview_items, _roommates);
        _listviewRoommates.setAdapter(_adapterRoommates);

        _listviewLocations = findViewById(R.id.listLocation);
        _adapterLocations = new AdapterInfomation(this, R.layout.listview_items, _locations);
        _listviewLocations.setAdapter(_adapterLocations);

        _listviewRoommates.setOnItemClickListener(remove_when_click_roommates);
        _listviewLocations.setOnItemClickListener(remove_when_click_locations);
        ImageButton btnAddRoommate = (ImageButton) findViewById(R.id.btnAddRoommate);
        btnAddRoommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, InfomationActivity.class);
                intent.putExtra("title", "Add New Roommate");
                startActivityForResult(intent, REQUEST_CODE_ADD_ROOMMATE);
            }
        });

        ImageButton btnAddLocation = (ImageButton) findViewById(R.id.btnAddLocation);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, InfomationActivity.class);
                intent.putExtra("title", "Add New Location");
                startActivityForResult(intent, REQUEST_CODE_ADD_LOCATION);
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
                if (_locations.size() == 0) {
                    Toast.makeText(getApplicationContext(), "There is no candidate location", Toast.LENGTH_LONG).show();
                    return;
                }
                intent.putExtra("people",_roommates);
                intent.putExtra("location", _locations);
                startActivityForResult(intent, REQUEST_CODE_FIND_BEST_LOCATION);
            }
        });
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
            case REQUEST_CODE_ADD_LOCATION: {
                if (resultCode == Activity.RESULT_OK) {
                    Infomation newLocation = (Infomation) data.getSerializableExtra("user");
                    _locations.add(newLocation);
                    updateListviewLocations();
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default: break;
        }
    }
}