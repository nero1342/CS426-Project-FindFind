package com.apcs.nero.findfind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EDIT_INFO = 0x9345;
    Button _btnCreateRoom, _btnJoinRoom, _btnEditInfo;
    Infomation _user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        _user = new Infomation("Nguyen E Ro", "419 Phan Xich Long");
        // Create Room
        _btnCreateRoom = (Button) findViewById(R.id.btnCreateRoom);
        _btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("user", _user);
                startActivity(intent);
            }
        });
        // Edit Info
        _btnEditInfo = (Button) findViewById(R.id.btnEditInfo);
        _btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfomationActivity.class);
                intent.putExtra("user", _user);
                startActivity(intent);

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