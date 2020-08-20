package com.apcs.nero.findfind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_ROOMMATE = 0x9345;
    private ArrayList<Infomation> _roommates = new ArrayList<>();
    private AdapterInfomation _adapter;
    private ListView _listviewRoommates = null;
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
    private void updateListview() {
        _adapter.notifyDataSetChanged();
    }

    private void initComponents() {
        _listviewRoommates = findViewById(R.id.listRoommate);
        _adapter = new AdapterInfomation(this, R.layout.listview_items, _roommates);
        _listviewRoommates.setAdapter(_adapter);
        Button btnAddRoommate = (Button) findViewById(R.id.btnAddRoommate);
        btnAddRoommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, InfomationActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_ROOMMATE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ROOMMATE) {
            if (resultCode == Activity.RESULT_OK) {
                Infomation newRoommate = (Infomation) data.getSerializableExtra("user");
                _roommates.add(newRoommate);
                updateListview();
            }
            else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}