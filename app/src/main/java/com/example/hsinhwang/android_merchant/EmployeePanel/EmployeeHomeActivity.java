package com.example.hsinhwang.android_merchant.EmployeePanel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Employees;

import com.example.hsinhwang.android_merchant.Classes.ImageEmployeeTask;
import com.example.hsinhwang.android_merchant.InstantEmployeePanel.EmployeeCleanService;
import com.example.hsinhwang.android_merchant.InstantEmployeePanel.EmployeeDinlingService;
import com.example.hsinhwang.android_merchant.InstantEmployeePanel.EmployeeRoomService;
import com.example.hsinhwang.android_merchant.ManagerPanel.ManagerHomeActivity;
import com.example.hsinhwang.android_merchant.ManagerPanel.RatingReviewActivity;
import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class EmployeeHomeActivity extends AppCompatActivity {
    private final static String TAG = "EmployeeHomeActivity";
    private LinearLayout employHomeBottom;
    private int idEmployee;
    private CommonTask employeeGetAllTask;
    private TextView txMyName, txMemberEmail, txPhoneNumber;
    private ImageView ivProfilePicture;
    private byte[] image;
    private Employees employee = null;
    private static final int REQUEST_TAKE_PICTURE_SMALL = 0;
    private static final int REQUEST_PICK_PICTURE = 1;
    private SharedPreferences preferences;
    private CommonTask empFindTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);
        initialization();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences(Common.EMPLOYEE_LOGIN, MODE_PRIVATE);
        idEmployee = pref.getInt("IdEmployee", 0);
        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE_SMALL:  //縮圖
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Bitmap picture = (Bitmap) bundle.get("data");
                        ivProfilePicture.setImageBitmap(picture);
                    }
                    break;
                case REQUEST_PICK_PICTURE:    //挑圖
                    employHomeBottom.removeAllViews();
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, columns,
                                null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String imagePath = cursor.getString(0);
                            cursor.close();
                            Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                            Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                            ivProfilePicture.setImageBitmap(downsizedImage);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            srcImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            image = out.toByteArray();

                            String imageBase64 = "";
                            if (image != null) imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);

                            if (Common.networkConnected(this)) {
                                String url = Common.URL + "/EmployeeServlet";
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "updateImage");
                                jsonObject.addProperty("idEmployee", idEmployee);
                                jsonObject.addProperty("imageBase64", imageBase64);

                                String jsonOut = jsonObject.toString();
                                empFindTask = new CommonTask(url, jsonOut);
                                try {
                                    String result = empFindTask.execute().get();
                                    int count = Integer.valueOf(result);
                                } catch (Exception e) {
//                                    Log.e(TAG, e.toString());
                                }
                            }
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ivProfilePicture.setEnabled(true);
                } else {
                    ivProfilePicture.setEnabled(false);
                }
                break;
        }
    }

    private void initialization() {
        employHomeBottom = findViewById(R.id.employHomeBottom);
        employHomeBottom.setPadding(5, 5, 5,5);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        txMyName = findViewById(R.id.txMyName);
        txMemberEmail = findViewById(R.id.txMemberEmail);
        txPhoneNumber = findViewById(R.id.txPhoneNumber);
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
            }

        });
    }

    /**
     * 部門功能
     */
    private void loadData() {
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/EmployeeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("idEmployee", idEmployee);
            String jsonOut = jsonObject.toString();
            employeeGetAllTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = employeeGetAllTask.execute().get();
                Type listType = new TypeToken<Employees>() {
                }.getType();
                employee = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }

            if (employee == null) {
                Common.showToast(this, R.string.msg_NoEmployeesFound);
            } else {
                SharedPreferences pref = getSharedPreferences(Common.EMPLOYEE_LOGIN, MODE_PRIVATE);
                pref.edit().putInt("idDepartment", employee.getDepartmentId());
                loadImage(idEmployee);
                insertDepartmentButton(employee.getDepartmentId());
                txMyName.setText(employee.getName());
                txMemberEmail.setText(employee.getEmail());
                txPhoneNumber.setText(employee.getPhone());
            }
        }
    }

    private void loadImage(int id) {
        String url = Common.URL + "/EmployeeServlet";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;

        try {
            bitmap = new ImageEmployeeTask(url, id, imageSize, ivProfilePicture).execute().get();
        } catch (Exception e) {
//            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivProfilePicture.setImageBitmap(bitmap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image = out.toByteArray();
        } else {
            ivProfilePicture.setImageResource(R.drawable.employee_pic);
        }
    }

    private void insertDepartmentButton(int departmentId) {
        // 設定按鈕尺寸/Margin
        LinearLayoutCompat.LayoutParams param = new LinearLayoutCompat.LayoutParams(240, 240);
        param.leftMargin = 20;

        switch (departmentId) {
            case 1: // 清潔
                Button cleanBtn = new Button(this);
                cleanBtn.setText("清潔進度");
                cleanBtn.setBackgroundColor(Color.parseColor("#F7DF96"));
                cleanBtn.setLayoutParams(param);
                cleanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employHomeBottom.removeAllViews();
                        Intent intent = new Intent(EmployeeHomeActivity.this, EmployeeCleanService.class);
                        // Intent intent = new Intent(EmployeeHomeActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                    }
                });
                employHomeBottom.addView(cleanBtn);
                break;
            case 2: // 房務
                Button roomBtn = new Button(this);
                roomBtn.setText("房務進度");
                roomBtn.setBackgroundColor(Color.parseColor("#F7DF96"));
                roomBtn.setLayoutParams(param);
                roomBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employHomeBottom.removeAllViews();
                        Intent intent = new Intent(EmployeeHomeActivity.this, EmployeeRoomService.class);
                        // Intent intent = new Intent(EmployeeHomeActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                    }
                });
                employHomeBottom.addView(roomBtn);
                break;
            case 3: // 餐飲
                Button dineBtn = new Button(this);
                dineBtn.setText("餐飲進度");
                dineBtn.setBackgroundColor(Color.parseColor("#F7DF96"));
                dineBtn.setLayoutParams(param);
                dineBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employHomeBottom.removeAllViews();
                        Intent intent = new Intent(EmployeeHomeActivity.this, EmployeeDinlingService.class);
                        // Intent intent = new Intent(EmployeeHomeActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                    }
                });
                employHomeBottom.addView(dineBtn);
                break;
            case 4: // 櫃檯
                Button conciergeBtn = new Button(this);
                conciergeBtn.setText("房務進度");
                conciergeBtn.setBackgroundColor(Color.parseColor("#F7DF96"));
                conciergeBtn.setLayoutParams(param);
                conciergeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employHomeBottom.removeAllViews();
                        Intent intent = new Intent(EmployeeHomeActivity.this, ManagerHomeActivity.class);
                        // Intent intent = new Intent(EmployeeHomeActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                    }
                });
                employHomeBottom.addView(conciergeBtn);
                break;
            case 5: // 主管
                // 動態生成按鈕
                Button managerBtn = new Button(this);
                managerBtn.setText("管理編輯");
                managerBtn.setBackgroundColor(Color.parseColor("#F7DF96"));
                managerBtn.setLayoutParams(param);
                managerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employHomeBottom.removeAllViews();
                        Intent intent = new Intent(EmployeeHomeActivity.this, ManagerHomeActivity.class);
                        startActivity(intent);
                    }
                });
                employHomeBottom.addView(managerBtn);

                Button reviewBtn = new Button(this);
                reviewBtn.setText("回覆評論");
                reviewBtn.setBackgroundColor(Color.parseColor("#F7DF96"));
                reviewBtn.setLayoutParams(param);
                reviewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        employHomeBottom.removeAllViews();
                        Intent intent = new Intent(EmployeeHomeActivity.this, RatingReviewActivity.class);
                        startActivity(intent);
                    }
                });
                employHomeBottom.addView(reviewBtn);
                break;


            default:
                Common.showToast(this, "cannot identify department");
                finish();
        }

        Button edit = new Button(this);
        edit.setText("編輯資料");
        edit.setBackgroundColor(Color.parseColor("#F7DF96"));
        edit.setLayoutParams(param);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employHomeBottom.removeAllViews();
                Intent intent = new Intent(EmployeeHomeActivity.this, EmployeeEditActivity.class);
                Bundle bundle = new Bundle();
                Employees emp = new Employees(employee.getId(), employee.getCode(), employee.getName(), employee.getPassword(),
                        employee.getEmail(), employee.getGender(), employee.getPhone(), employee.getAddress(), employee.getDepartmentId());
                bundle.putSerializable("employee", emp);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        employHomeBottom.addView(edit);

        Button logout = new Button(this);
        logout.setText("登出");
        logout.setBackgroundColor(Color.parseColor("#F7DF96"));
        logout.setLayoutParams(param);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employHomeBottom.removeAllViews();
                SharedPreferences pref = getSharedPreferences(Common.EMPLOYEE_LOGIN, MODE_PRIVATE);
                SharedPreferences page = getSharedPreferences(Common.PAGE, MODE_PRIVATE);
                pref.edit().putBoolean("login", false).putString("email", "").putString("password", "").putInt("IdEmployee", 0).apply();
                page.edit().putInt("page", 1).apply();
                finish();
            }
        });
        employHomeBottom.addView(logout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
