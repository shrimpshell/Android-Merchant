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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Employees;
import com.example.hsinhwang.android_merchant.Classes.ImageTask;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

public class ManagerEmployeeEditActivity extends AppCompatActivity {
    private final static String TAG = "EmployeeEditActivity";
    private TextView employeeCode, employeeGender;
    private EditText etEmployeeName, etEmployeeEmail, etEmployeePass, etEmployeePhone, etEmployeeAddress;
    private RadioGroup employeeEditGenderGroup;
    private Button btnEmployeeSubmit;
    private ImageView employeePic;
    private Spinner spinnerDepartment;
    private byte[] image;
    private static final int REQUEST_PICK_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_employee_edit);
        initialization();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            Uri uri = intent.getData();
            if (uri != null) {
                String[] columns = {MediaStore.Images.Media.DATA};
                Cursor cursor = ManagerEmployeeEditActivity.this.getContentResolver().query(uri, columns,
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
        employeeEditGenderGroup = findViewById(R.id.employeeEditGenderGroup);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        employeeCode = findViewById(R.id.employeeCode);

        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail);
        etEmployeePass = findViewById(R.id.etEmployeePass);
        etEmployeePhone = findViewById(R.id.etEmployeePhone);
        etEmployeeAddress = findViewById(R.id.etEmployeeAddress);

        btnEmployeeSubmit = findViewById(R.id.btnEmployeeSubmit);
    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final Object obj = bundle.getSerializable("employee");
            if (obj != null) {
                final Employees employee = (Employees) obj;

                loadImage(((Employees) obj).getId());
                spinnerDepartment.setSelection(((Employees) obj).getDepartmentId() - 1);

                employeeCode.setText(employee.getCode());
                if (((Employees) obj).getGender().equals("MALE")) {
                    employeeEditGenderGroup.check(R.id.Male);
                } else {
                    employeeEditGenderGroup.check(R.id.Female);
                }

                etEmployeeName.setText(employee.getName());
                etEmployeeEmail.setText(employee.getEmail());
                etEmployeePass.setText(employee.getPassword());
                etEmployeePhone.setText(employee.getPhone());
                etEmployeeAddress.setText(employee.getAddress());

                btnEmployeeSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateEmployee(((Employees) obj).getId());
                        finish();
                    }
                });
            }
        }
    }

    private void loadImage(int id) {
        String url = Common.URL + "/EmployeeServlet";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;

        try {
            bitmap = new ImageTask(url, id, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            employeePic.setImageBitmap(bitmap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image = out.toByteArray();
        } else {
            employeePic.setImageResource(R.drawable.employee_pic);
        }
    }

    private void updateEmployee(int id) {
        String genderStr = "";
        int gender = employeeEditGenderGroup.getCheckedRadioButtonId();
        switch (gender) {
            case R.id.Male:
                genderStr = "MALE";
                break;
            case R.id.Female:
                genderStr = "FEMALE";
                break;
            default:
                Toast.makeText(this, "請選擇性別", Toast.LENGTH_SHORT);
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
        String code = employeeCode.getText().toString().trim(),
                name = etEmployeeName.getText().toString().trim(),
                pass = etEmployeePass.getText().toString().trim(),
                email = etEmployeeEmail.getText().toString().trim(),
                phone = etEmployeePhone.getText().toString().trim(),
                address = etEmployeeAddress.getText().toString().trim();

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/EmployeeServlet";
            Employees employee = new Employees(id, code, name, pass, email, genderStr, phone, address, department);
            String imageBase64 = "";
            if (image != null) imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            Log.e(TAG, new Gson().toJson(employee));
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "employeeUpdate");
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
                Common.showToast(this, R.string.msg_UpdateFail);
            } else {
                Common.showToast(this, R.string.msg_UpdateSuccess);
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }
}
