package com.apcs.nero.findfind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EDIT_INFO = 0x9345;
    ImageButton _btnCreateRoom, _btnJoinRoom, _btnEditInfo;
    Infomation _user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        _user = new Infomation("Nguyen E Ro", new LocationInfo(new LatLong(10.8029884,106.679072), "419 Phan Xich Long, Phuong 3, Phu Nhuan, HCM"));
        // Create Room
        _btnCreateRoom = (ImageButton) findViewById(R.id.btnCreateRoom);
        _btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("user", _user);
                startActivity(intent);
            }
        });
        // Join Room
        _btnJoinRoom = (ImageButton) findViewById(R.id.btnJoinRoom);
        _btnJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Coming soon...", Toast.LENGTH_LONG).show();
            }
        });

        // Edit Info
        _btnEditInfo = (ImageButton) findViewById(R.id.btnEditInfo);
        _btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfomationActivity.class);
                intent.putExtra("user", _user);
                intent.putExtra("title","Edit Infomation");
                startActivityForResult(intent, REQUEST_CODE_EDIT_INFO);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_INFO) {
            if (resultCode == Activity.RESULT_OK) {
                _user = (Infomation) data.getSerializableExtra("user");
            }
            else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}