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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Events;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

public class AddEventActivity extends AppCompatActivity {
    private final static String TAG = "AddEventActivity";
    private EditText etAddEventName, etAddEventDescription, etAddEventDiscount;
    private DatePicker etAddEventStartTime, etAddEventEndTime;
    private Button btnAddEvent;
    private ImageView ivEvent;
    private byte[] image;
    private static final int REQUEST_PICK_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        initialization();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Uri uri = intent.getData();
            int newSize = 512;
            if (uri != null) {
                String[] columns = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, columns,
                        null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String imagePath = cursor.getString(0);
                    cursor.close();
                    Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                    Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                    ivEvent.setImageBitmap(downsizedImage);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    srcImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    image = out.toByteArray();
                }
            }
        }
    }

    public void initialization() {
        ivEvent = findViewById(R.id.ivEvent);
        etAddEventName = findViewById(R.id.etAddEventName);
        etAddEventDescription = findViewById(R.id.etAddEventDescription);
        etAddEventStartTime = findViewById(R.id.etAddEventStartTime);
        etAddEventEndTime = findViewById(R.id.etAddEventEndTime);
        etAddEventDiscount = findViewById(R.id.etAddEventDiscount);

        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertAction();
                finish();
            }
        });
        ivEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ivEvent.setEnabled(true);
                } else {
                    ivEvent.setEnabled(false);
                }
                break;
        }
    }

    private void insertAction() {
        String name = etAddEventName.getText().toString().trim(),
                description = etAddEventDescription.getText().toString().trim();
        float discount = Float.parseFloat(etAddEventDiscount.getText().toString());
        String start = (etAddEventStartTime.getYear()) + "-" + ((etAddEventStartTime.getMonth()) + 1 > 9 ? etAddEventStartTime.getMonth() + 1 : "0" + (etAddEventStartTime.getMonth() + 1)) + "-" + etAddEventStartTime.getDayOfMonth(),
                end = (etAddEventEndTime.getYear()) + "-" + ((etAddEventEndTime.getMonth() + 1) > 9 ? etAddEventEndTime.getMonth() + 1 : "0" + (etAddEventEndTime.getMonth() + 1)) + "-" + etAddEventEndTime.getDayOfMonth();
        if (name.length() == 0 || description.length() == 0 || etAddEventDiscount.getText().toString().length() == 0 || start.length() == 0|| end.length() == 0) {
            Common.showToast(AddEventActivity.this, "請勿留空值");
            return;
        }
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/EventServlet";
            Events event = new Events(0, name, description, start, end, discount);
            String imageBase64 = "";
            if (image != null) imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            Log.e(TAG, new Gson().toJson(event));
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "eventInsert");
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
                Common.showToast(this, R.string.msg_InsertFail);
            } else {
                Common.showToast(this, R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }
}
