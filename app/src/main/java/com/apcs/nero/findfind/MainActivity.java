package com.apcs.nero.findfind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EDIT_INFO = 0x9345;
    ImageButton _btnCreateRoom, _btnJoinRoom, _btnEditInfo;
    Infomation _user;
    LinearLayout _layoutJoinRoom;
    GridLayout _layoutMenu;
    BackendRoom _backendRoom;

    TextView _textViewStatus;

    Integer isProcess = new Integer(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void initComponents() {
        _textViewStatus = (TextView) findViewById(R.id.txtTemp);
        _layoutMenu = (GridLayout) findViewById(R.id.layoutMenu);
        _layoutJoinRoom = (LinearLayout) findViewById(R.id.layoutJoinRoom);
        _layoutJoinRoom.setVisibility(View.INVISIBLE);

        _backendRoom = new BackendRoom(getApplicationContext(), _textViewStatus, MainActivity.this, isProcess);

        _user = new Infomation("Nguyen E Ro", new LocationInfo(new LatLong(10.8029884,106.679072), "419 Phan Xich Long, Phuong 3, Phu Nhuan, HCM"));
        // Create Room
        _btnCreateRoom = (ImageButton) findViewById(R.id.btnCreateRoom);

        _btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new room with roomID
                isProcess = 1;
                _backendRoom.createRoomIDFromServer();
            }
        });
        _textViewStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                int roomID = Integer.parseInt(s.toString());
                switch (isProcess) {
                    case 2: //
                        isProcess = 0;
                        if (roomID == -1) {
                            Toast.makeText(getApplicationContext(), "This room is unavailable. Please check it again.", Toast.LENGTH_SHORT).show();
                        }
                    case 1: // Create new room
                        isProcess = 0;
                        if (roomID != -1) {
                            Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                            intent.putExtra("user", _user);
                            intent.putExtra("roomID", roomID);
                            intent.putExtra("backendRoomIP", _backendRoom.getIP_ADDRESS());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't create a new room", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
        //
        // Join Room
        _btnJoinRoom = (ImageButton) findViewById(R.id.btnJoinRoom);
        _btnJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layoutJoinRoom.setVisibility(View.VISIBLE);
                _layoutMenu.setVisibility(View.INVISIBLE);
                EditText editText = findViewById(R.id.editTextJoinRoomID);
                editText.requestFocus();
                showKeyboard();
                //Toast.makeText(getApplicationContext(), "Coming soon...", Toast.LENGTH_LONG).show();
            }
        });
        Button _btnCancelJoinRoom = (Button) findViewById(R.id.btnCancelJoinRoom);
        _btnCancelJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _layoutMenu.setVisibility(View.VISIBLE);
                _layoutJoinRoom.setVisibility(View.INVISIBLE);
                closeKeyboard();
            }
        });
        Button _btnJoinRoom2 = (Button) findViewById(R.id.btnJoinRoom2);
        _btnJoinRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextRoomID = (EditText) findViewById(R.id.editTextJoinRoomID);
                try {
                    isProcess = 2;
                    int roomID = Integer.parseInt(editTextRoomID.getText().toString());
                    _backendRoom.checkValidRoom(roomID);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Please input correct roomID", Toast.LENGTH_LONG).show();
                }
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