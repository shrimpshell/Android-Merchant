package com.example.hsinhwang.android_merchant.ManagerPanel;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Events;
import com.example.hsinhwang.android_merchant.Classes.ImageTask;
import com.example.hsinhwang.android_merchant.Classes.Rooms;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

public class ManagerEditActivity extends AppCompatActivity {
    private final static String TAG = "EditActivity";
    private EditText etName, etDescription, etStartTime, etEndTime, etRoomSize, etBed, etAdult, etChild, etQuantity, etPrice, etDiscount;
    private LinearLayout eventElement, roomElement;
    private Button btnSubmit;
    private ImageView imageView;
    private byte[] image;
    private static final int REQUEST_PICK_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_edit);
        initialization();
        loadData();

    }

    @Override
    public void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageView.setEnabled(true);
                } else {
                    imageView.setEnabled(false);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            Uri uri = intent.getData();
            if (uri != null) {
                String[] columns = {MediaStore.Images.Media.DATA};
                Cursor cursor = ManagerEditActivity.this.getContentResolver().query(uri, columns,
                        null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String imagePath = cursor.getString(0);
                    cursor.close();
                    Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                    Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                    imageView.setImageBitmap(downsizedImage);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    srcImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    image = out.toByteArray();
                }
            }
        }
    }

    private void initialization() {
        imageView = findViewById(R.id.imageView);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        eventElement = findViewById(R.id.eventElement);
        btnSubmit = findViewById(R.id.btnSubmit);

        roomElement = findViewById(R.id.roomElement);
        etRoomSize = findViewById(R.id.etRoomSize);
        etBed = findViewById(R.id.etBed);
        etAdult = findViewById(R.id.etAdult);
        etChild = findViewById(R.id.etChild);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        etDiscount = findViewById(R.id.etDiscount);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
            }
        });
    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Object event = bundle.getSerializable("event");
            Object room = bundle.getSerializable("room");
            if (event != null) {
                final Events obj = (Events) event;
                String start = obj.getStart(), end = obj.getEnd();

                eventElement.setVisibility(View.VISIBLE);
                loadEventImage(obj.getEventId());
                etName.setText(obj.getName());
                etDescription.setText(obj.getDescription());
                etDiscount.setText(String.valueOf(obj.getDiscount()));
                etStartTime.setText(start);
                etEndTime.setText(end);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float discount = 0.0f;
                        String name = etName.getText().toString();
                        if (name.length() <= 0) {
                            Common.showToast(ManagerEditActivity.this, R.string.msg_NameIsInvalid);
                            return;
                        }
                        try {
                            discount = Float.parseFloat(etDiscount.getText().toString());
                        } catch (NumberFormatException e) {
                            Common.showToast(ManagerEditActivity.this, "格式錯誤");
                        }

                        String description = etDescription.getText().toString();
                        String start = etStartTime.getText().toString(), end = etEndTime.getText().toString();

                        if (Common.networkConnected(ManagerEditActivity.this)) {
                            String url = Common.URL + "/EventServlet";
                            Events event = new Events(obj.getEventId(), name, description, start, end, discount);
                            String imageBase64 = "";
                            if (image != null) imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "eventUpdate");
                            jsonObject.addProperty("event", new Gson().toJson(event));
                            jsonObject.addProperty("imageBase64", imageBase64);
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(ManagerEditActivity.this, R.string.msg_UpdateFail);
                            } else {
                                Common.showToast(ManagerEditActivity.this, R.string.msg_UpdateSuccess);
                            }
                        } else {
                            Common.showToast(ManagerEditActivity.this, R.string.msg_NoNetwork);
                        }
                        finish();
                    }
                });

            } else {
                roomElement.setVisibility(View.VISIBLE);
                final Rooms obj = (Rooms) room;

                String roomSize = obj.getRoomSize().substring(0, obj.getRoomSize().indexOf("平")),
                bed = obj.getBed().substring(0, obj.getBed().indexOf("張"));

                loadRoomImage(obj.getId());
                etName.setText(obj.getName());
                etRoomSize.setText(roomSize);
                etBed.setText(bed);
                etAdult.setText(String.valueOf(obj.getAdultQuantity()));
                etChild.setText(String.valueOf(obj.getChildQuantity()));
                etQuantity.setText(String.valueOf(obj.getRoomQuantity()));
                etPrice.setText(String.valueOf(obj.getPrice()));

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        if (name.length() <= 0) {
                            Common.showToast(ManagerEditActivity.this, R.string.msg_NameIsInvalid);
                            return;
                        }
                        String roomSize = etRoomSize.getText().toString() + "平方公尺",
                                bed = etBed.getText().toString() + "張雙人床";
                        int adult = Integer.parseInt(etAdult.getText().toString()),
                                child = Integer.parseInt(etChild.getText().toString()),
                                quantity = Integer.parseInt(etQuantity.getText().toString()),
                                price = Integer.parseInt(etPrice.getText().toString());
                        if (Common.networkConnected(ManagerEditActivity.this)) {
                            String url = Common.URL + "/RoomServlet";
                            Rooms room = new Rooms(obj.getId(), name, roomSize, bed, adult, child, quantity, price);
                            String imageBase64 = "";
                            if (image != null) imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "roomUpdate");
                            jsonObject.addProperty("room", new Gson().toJson(room));
                            jsonObject.addProperty("imageBase64", imageBase64);
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(ManagerEditActivity.this, R.string.msg_UpdateFail);
                            } else {
                                Common.showToast(ManagerEditActivity.this, R.string.msg_UpdateSuccess);
                            }
                        } else {
                            Common.showToast(ManagerEditActivity.this, R.string.msg_NoNetwork);
                        }
                        finish();
                    }
                });
            }
        }
    }

    private void loadRoomImage(int id) {
        String url = Common.URL + "/RoomServlet";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;

        try {
            bitmap = new ImageTask(url, id, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image = out.toByteArray();
        } else {
            imageView.setImageResource(R.drawable.room_review);
        }
    }

    private void loadEventImage(int id) {
        String url = Common.URL + "/EventServlet";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;

        try {
            bitmap = new ImageTask(url, id, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image = out.toByteArray();
        } else {
            imageView.setImageResource(R.drawable.events);
        }
    }
}
