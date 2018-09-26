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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Employees;
import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

public class AddEmployeeActivity extends AppCompatActivity {
    private final static String TAG = "AddEmployeeActivity";
    private RadioGroup employeeAddGenderGroup;
    private EditText etEmployeeAddName, etEmployeeAddEmail, etEmployeeAddPass, etEmployeeAddPhone, etEmployeeAddAddress;
    private Button btnEmployeeAdd;
    private Spinner spinnerDepartment;
    private ImageView employeePic;
    private byte[] image;
    private static final int REQUEST_PICK_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
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
                    employeePic.setImageBitmap(downsizedImage);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    srcImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    image = out.toByteArray();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    employeePic.setEnabled(true);
                } else {
                    employeePic.setEnabled(false);
                }
                break;
        }
    }

    private void initialization() {
        employeePic = findViewById(R.id.employeePic);
        employeePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
            }
        });
        employeeAddGenderGroup = findViewById(R.id.employeeAddGenderGroup);
        etEmployeeAddName = findViewById(R.id.etEmployeeAddName);
        etEmployeeAddEmail = findViewById(R.id.etEmployeeAddEmail);
        etEmployeeAddPass = findViewById(R.id.etEmployeeAddPass);
        etEmployeeAddPhone = findViewById(R.id.etEmployeeAddPhone);
        etEmployeeAddAddress = findViewById(R.id.etEmployeeAddAddress);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        btnEmployeeAdd = findViewById(R.id.btnEmployeeAdd);
        btnEmployeeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertEmployee();
                finish();
            }
        });
    }

    private void insertEmployee() {
        String genderStr = "";
        int gender = employeeAddGenderGroup.getCheckedRadioButtonId();
        switch (gender) {
            case R.id.Male:
                genderStr = "MALE";
                break;
            case R.id.Female:
                genderStr = "FEMALE";
                break;
            default:
                Toast.makeText(AddEmployeeActivity.this, "請選擇性別", Toast.LENGTH_SHORT);
        }
        int department = 1;
        String departmentStr = (String) spinnerDepartment.getSelectedItem();
        switch (departmentStr) {
            case "清潔部門":
            case "Clean Service":
                department = 1;
                break;
            case "房務部門":
            case "Room Service":
                department = 2;
                break;
            case "餐飲部門":
            case "Meal Service":
                department = 3;
                break;
            case "櫃檯部門":
            case "Concierge Service":
                department = 4;
                break;
            case "主管":
            case "Manager":
                department = 5;
                break;
        }
        String name = etEmployeeAddName.getText().toString().trim(),
                pass = etEmployeeAddPass.getText().toString().trim(),
                email = etEmployeeAddEmail.getText().toString().trim(),
                phone = etEmployeeAddPhone.getText().toString(),
                address = etEmployeeAddAddress.getText().toString();

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/EmployeeServlet";
            Employees employee = new Employees(0, email, name, pass, email, genderStr, phone, address, department);
            String imageBase64 = "";
            if (image != null) imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "employeeInsert");
            jsonObject.addProperty("employee", new Gson().toJson(employee));
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
